// https://grafana.com/docs/k6/latest/testing-guides/test-types/smoke-testing/#smoke-testing-in-k6
// https://grafana.com/docs/k6/latest/using-k6/scenarios/executors/ramping-vus/#get-the-stage-index
import { check } from 'k6';
import http from 'k6/http';
import { handleSummary as handleSummary_helper } from './handleSummary.js';
import { login } from './utils.js';

const baseUrl = __ENV.BASE_URL;
const TARGET_RATE = 20;

const SCENARIOS = {
    // Block 0: Warmup — fills connection pool, JIT-compiles hot paths,
    // populates Hibernate query plan cache. Discard from analysis.
    "0_warmup": {
        executor: 'constant-arrival-rate',
        rate: TARGET_RATE,
        timeUnit: '1s',
        duration: '2m',
        preAllocatedVUs: 50,
        maxVUs: 100,
        startTime: '0s',
        gracefulStop: '5s',
        tags: { is_target: 'false' },
    },
    "1_measurement": {
        executor: 'constant-arrival-rate',
        rate: TARGET_RATE,
        timeUnit: '1s',
        duration: '1m',
        preAllocatedVUs: 50,
        maxVUs: 100,
        startTime: '2m10s',
        gracefulStop: '5s',
        tags: { is_target: 'true' },
    },
    "2_measurement": {
        executor: 'constant-arrival-rate',
        rate: TARGET_RATE,
        timeUnit: '1s',
        duration: '1m',
        preAllocatedVUs: 50,
        maxVUs: 100,
        startTime: '3m20s',
        gracefulStop: '5s',
        tags: { is_target: 'true' },
    },
    "3_measurement": {
        executor: 'constant-arrival-rate',
        rate: TARGET_RATE,
        timeUnit: '1s',
        duration: '1m',
        preAllocatedVUs: 50,
        maxVUs: 100,
        startTime: '4m30s',
        gracefulStop: '5s',
        tags: { is_target: 'true' },
    },
    "4_measurement": {
        executor: 'constant-arrival-rate',
        rate: TARGET_RATE,
        timeUnit: '1s',
        duration: '1m',
        preAllocatedVUs: 50,
        maxVUs: 100,
        startTime: '5m40s',
        gracefulStop: '5s',
        tags: { is_target: 'true' },
    },
};

const getUri = (data) => `/trip/${data.tripId}/reservation/analysis/text`

// Sample reservation payload with flightTicket field populated
const sampleReservation = {
    category: "FLIGHT_TICKET",
    code: "TEST-CODE-123",
    primaryHrefLink: "https://example.com/reservation/123",
    flightTicket: {
        flightNumber: "TA123",
        departureDateTimeIsoString: "2026-09-08T10:00:00Z",
        passengerName: "John Doe"
    }
}


export const options = {
    scenarios: SCENARIOS,
    thresholds: {
        http_req_duration: ['p(95)<5000'], // 10s (the blocking baseline's non-stressed stage latency is ~5 sec)
        http_req_failed: ['rate<0.05'],
    },
};

export function setup() {
    return login()
}

export default (data) => {

    const params = {
        headers: {
            // ...getHeader(currentScenario),
            'Content-Type': 'application/json',
        },
    };

    // 1. POST list of 2 reservations
    const payload = [sampleReservation, sampleReservation];
    const postUri = `/trip/${data.tripId}/reservation/batch`;
    const postRes = http.post(`${baseUrl}${postUri}`, JSON.stringify(payload), params);

    check(postRes, { 'post status returned 201': (r) => r.status == 201 });

    if (postRes.status !== 201) return;

    // Extract IDs from response
    const createdReservations = postRes.json();
    const ids = createdReservations.map(r => r.id).join(',');

    // 2. GET list of reservation using list of id as param
    const getUri = `/reservations?ids=${ids}`;
    const getRes = http.get(`${baseUrl}${getUri}`, params);

    check(getRes, { 'get status returned 200': (r) => r.status == 200 });
};

export function handleSummary(data) {
    return handleSummary_helper(data, SCENARIOS, getUri)
}