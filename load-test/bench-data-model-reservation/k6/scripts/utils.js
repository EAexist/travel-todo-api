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

export function getHeader(stage_index) {
    return ({
        'Load-Test-Stage-Id': `stage_${stage_index}`,
    })
}