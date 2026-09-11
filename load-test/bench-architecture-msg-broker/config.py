import os
from datetime import datetime
from pathlib import Path

from pydantic import computed_field
from pydantic_settings import BaseSettings, SettingsConfigDict

_env_stage = os.getenv("ENV", "default")
_env_file = ".env.dev" if _env_stage == "dev" else ".env"

K6_SCRIPT_DIR = Path(__file__).resolve().parent / "k6" / "scripts"


class Config(BaseSettings):
    k6_scenario: str = "main"
    n_iterations: int = 4
    test_id: str = ""
    iamge_tag_base: str = "bench-architecture-msg-broker"
    is_benchmark: bool = True
    variant: str = ""

    @computed_field
    @property
    def k6_script_path(self) -> str:
        match self.k6_scenario:
            case "smoke":
                return K6_SCRIPT_DIR / "001_smoke.js"
            case "smoke_report":
                return K6_SCRIPT_DIR / "002_smoke_report.js"
            case "probe":
                return K6_SCRIPT_DIR / "003_probe_breakpoint.js"
            case _:
                return K6_SCRIPT_DIR / "000_main.js"

    @computed_field
    @property
    def output_dir(self) -> Path:
        return Path(__file__).resolve().parent / f"output/{self.test_id}"

    @computed_field
    @property
    def base_output_dir(self) -> Path:
        return self.output_dir / "baseline"

    @computed_field
    @property
    def candidate_output_dir(self) -> Path:
        return self.output_dir / "candidate"

    def model_post_init(self, __context):
        # Fires ONCE when Config() is instantiated
        if not self.test_id:
            now_str = datetime.now().strftime("%Y%m%d_%H%M%S")
            self.test_id = f"{self.k6_scenario}_{now_str}"

    model_config = SettingsConfigDict(
        env_file=_env_file,
        env_file_encoding="utf-8",
        extra="ignore",
    )


config = Config()
