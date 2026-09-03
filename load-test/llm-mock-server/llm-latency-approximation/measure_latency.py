import asyncio
import csv
import json
import os
import random
import re
import time
from collections import defaultdict
from datetime import datetime
from pathlib import Path

import httpx
from dotenv import load_dotenv
from schema import ExtractReservationChatResult
from scipy.stats import gamma

load_dotenv()

API_KEY = os.getenv("GOOGLE_API_KEY", "").strip()
MODEL_NAME = os.getenv("GEMINI_MODEL", "").strip()
ITERATIONS = int(os.getenv("ITERATIONS", 10))
MAX_CONCURRENT_CALLS = int(os.getenv("MAX_CONCURRENT_CALLS", 5))
SAMPLES_MANIFEST_PATH = Path(os.getenv("SAMPLES_MANIFEST_PATH", "").strip()).resolve()

if not API_KEY:
    raise ValueError("GOOGLE_API_KEY environment variable not set")
if not MODEL_NAME:
    raise ValueError("GEMINI_MODEL environment variable not set")

PROMPT_TEMPLATE_PATH = Path(__file__).resolve().parent / "prompts" / "template.md"
timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
# Convert f-string to Path object explicitly
OUTPUT_DIR = Path(__file__).resolve().parent / "output" / f"{timestamp}"
OUTPUT_FILE = OUTPUT_DIR / "latency_results.csv"
MODEL_OUTPUT_FILE = OUTPUT_DIR / "gamma_model_parameters.csv"
LATEST_POINTER_FILE = Path("./output/latest.json")

# Ensure output directory exists
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

API_URL = f"https://generativelanguage.googleapis.com/v1beta/models/{MODEL_NAME}:generateContent?key={API_KEY}"


def dereference_schema(schema: dict) -> dict:
    defs = schema.get("$defs", {})

    def resolve(node):
        if isinstance(node, dict):
            if "$ref" in node:
                ref_key = node["$ref"].split("/")[-1]
                resolved = resolve(defs[ref_key].copy()) if ref_key in defs else node
                return {**resolved, **{k: v for k, v in node.items() if k != "$ref"}}
            return {
                k: resolve(v)
                for k, v in node.items()
                if k not in ["$defs", "title", "additionalProperties", "description"]
            }
        return [resolve(i) for i in node] if isinstance(node, list) else node

    return resolve(schema)


# Pre-build static artifacts outside execution loop
FORMATTED_SCHEMA = dereference_schema(ExtractReservationChatResult.model_json_schema())
TEMPLATE = Path(PROMPT_TEMPLATE_PATH).read_text(encoding="utf-8")


# 3. Pre-load & Sanitize Time
def load_prepared_prompts() -> list[dict]:
    with open(SAMPLES_MANIFEST_PATH, "r", encoding="utf-8") as f:
        manifest = json.load(f)

    prepared = []
    for sample in manifest["samples"]:
        # Path is relative to manifest, which is in the project root
        f = Path(SAMPLES_MANIFEST_PATH).parent / sample["path"]
        if not f.exists():
            print(f"Warning: File {f} not found, skipping.")
            continue

        raw_text = f.read_text(encoding="utf-8", errors="ignore")
        # clean_text = re.sub(r"[\x00-\x08\x0b\x0c\x0e-\x1f\x7f]", "", raw_text).strip()
        clean_text = raw_text
        prepared.append(
            {"id": sample["id"], "prompt": TEMPLATE.format(input_data=clean_text)}
        )
    return prepared


# 4. Latency Measurement Task
async def measure(
    i: int, sample: dict, semaphore: asyncio.Semaphore, client: httpx.AsyncClient
) -> dict:
    prompt = sample["prompt"]
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {
            "responseMimeType": "application/json",
            "responseSchema": FORMATTED_SCHEMA,
        },
    }
    result = {
        "timestamp": datetime.now().isoformat(),
        "iteration": i,
        "sample_id": sample["id"],
        "latency_seconds": None,
        "input_tokens": None,
        "output_tokens": None,
        "error": None,
    }

    async with semaphore:
        try:
            start = time.perf_counter()
            resp = await client.post(API_URL, json=payload, timeout=60.0)
            result["latency_seconds"] = time.perf_counter() - start
            resp.raise_for_status()

            # Parse token usage
            data = resp.json()
            usage = data.get("usageMetadata", {})
            result["input_tokens"] = usage.get("promptTokenCount")
            result["output_tokens"] = usage.get("candidatesTokenCount")

            print(
                f"Iteration {i}/{ITERATIONS} ({sample['id']}): {result['latency_seconds']:.4f}s (In: {result['input_tokens']}, Out: {result['output_tokens']})"
            )
        except Exception as e:
            result["error"] = str(e)
            print(f"Iteration {i}/{ITERATIONS} ({sample['id']}): FAILED - {e}")
    return result


def fit_gamma_models():
    """Reads latency data, fits Gamma distribution per sample_id, and saves parameters."""
    if not OUTPUT_FILE.exists():
        print(f"No data file found at {OUTPUT_FILE}, skipping modeling.")
        return

    data_by_sample = defaultdict(list)

    with open(OUTPUT_FILE, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            if row["latency_seconds"] and not row["error"]:
                data_by_sample[row["sample_id"]].append(float(row["latency_seconds"]))

    model_params = []
    for sample_id, latencies in data_by_sample.items():
        if len(latencies) < 2:
            print(f"Skipping {sample_id}: not enough data points ({len(latencies)}).")
            continue

        # MLE Gamma fit (floc=0 forces the distribution to start at 0)
        alpha, loc, beta = gamma.fit(latencies, floc=0)
        model_params.append({"sample_id": sample_id, "alpha": alpha, "beta": beta})

    with open(MODEL_OUTPUT_FILE, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=["sample_id", "alpha", "beta"])
        writer.writeheader()
        writer.writerows(model_params)

    # Update latest.json pointer
    with open(LATEST_POINTER_FILE, "w", encoding="utf-8") as f:
        json.dump({"model_parameters": str(MODEL_OUTPUT_FILE.absolute())}, f, indent=2)

    print(f"\nGamma model parameters saved to {MODEL_OUTPUT_FILE}")
    print(f"Updated latest pointer at {LATEST_POINTER_FILE}")


async def main():
    prompts = load_prepared_prompts()
    semaphore = asyncio.Semaphore(MAX_CONCURRENT_CALLS)

    print(
        f"Measuring {ITERATIONS} full iterations (max concurrent: {MAX_CONCURRENT_CALLS}) on '{MODEL_NAME}'..."
    )

    file_exists = os.path.exists(OUTPUT_FILE)
    fieldnames = [
        "timestamp",
        "iteration",
        "sample_id",
        "latency_seconds",
        "input_tokens",
        "output_tokens",
        "error",
    ]

    async with httpx.AsyncClient() as client:
        tasks = []
        for iteration in range(ITERATIONS):
            prompt_indices = list(range(len(prompts)))
            random.shuffle(prompt_indices)

            for idx in prompt_indices:
                tasks.append(measure(iteration + 1, prompts[idx], semaphore, client))

        results = await asyncio.gather(*tasks)

    # Write CSV safely after all results are collected
    with open(OUTPUT_FILE, "a", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        if not file_exists:
            writer.writeheader()
        writer.writerows(results)

    print(f"\nResults saved to {OUTPUT_FILE}")

    # Run modeling post-processing
    fit_gamma_models()


if __name__ == "__main__":
    asyncio.run(main())
