// https://grafana.com/docs/k6/latest/testing-guides/test-types/smoke-testing/#smoke-testing-in-k6
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import http from 'k6/http';

const baseUrl = __ENV.BASE_URL;
const fixturesRoot = __ENV.FIXTURES_ROOT;

export function login(maxRetries = 10, delayMs = 3000) {
    let attempts = 0;

    while (attempts < maxRetries) {
        attempts++;

        const res = http.post(`${baseUrl}/auth/admin`, JSON.stringify({}), {
            headers: { 'Content-Type': 'application/json' },
            responseType: 'text',
        });

        // 1. Validate response status
        const isOk = check(res, { 'login status is 200': (r) => r.status === 200 });

        if (isOk && res.body) {
            try {
                // 2. Safe JSON parsing
                const bodyData = res.json();
                const tripId = bodyData?.tripSummary?.[0]?.id;

                if (tripId) {
                    return { tripId };
                } else {
                    console.warn(`[Attempt ${attempts}] Empty or unexpected payload structure: ${res.body}`);
                }
            } catch (err) {
                console.warn(`[Attempt ${attempts}] JSON parsing failed: ${err.message}`);
            }
        } else {
            console.warn(`[Attempt ${attempts}] Login failed with status: ${res.status}`);
        }

        // Wait before retrying
        sleep(delayMs / 1000);
    }

    // Fail hard if all attempts are exhausted
    throw new Error(`Failed to log in and retrieve tripId after ${maxRetries} attempts.`);
}

// @RequestBody CreateReservationDTO (ReservationCategory category, String confirmationText) createReservationDTO
const PAYLOADS = new SharedArray('samplesData', function () {
    const manifest = JSON.parse(open(`./${fixturesRoot}/samples.json`));
    return manifest.samples.map((item) => ({
        category: "UNKNOWN",
        confirmationText: `${open(`./${fixturesRoot}/${item.path}`)}\n<mock_data_id>${item.id}</<mock_data_id>`,
    }));
});

export function getPayload(vuId) {
    const payloadIndex = (vuId - 1) % PAYLOADS.length;
    return PAYLOADS[payloadIndex]
}

export function getHeader(stage_index) {
    return ({
        'Load-Test-Stage-Id': `stage_${stage_index}`,
    })
}