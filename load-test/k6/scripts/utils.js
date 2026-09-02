// https://grafana.com/docs/k6/latest/testing-guides/test-types/smoke-testing/#smoke-testing-in-k6
import { check } from 'k6';
import { SharedArray } from 'k6/data';
import http from 'k6/http';

const baseUrl = __ENV.BASE_URL;
const fixturesRoot = __ENV.FIXTURES_ROOT;

export function login() {
    const res = http.post(`${baseUrl}/auth/admin`, {
        headers: { 'Content-Type': "application/json" },
    });

    check(res, { 'login status is 200': (r) => r.status === 200 });

    const tripId = res.json().tripSummary[0].id
    return { tripId: tripId };
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