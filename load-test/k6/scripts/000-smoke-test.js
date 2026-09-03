// https://grafana.com/docs/k6/latest/testing-guides/test-types/smoke-testing/#smoke-testing-in-k6
// https://grafana.com/docs/k6/latest/using-k6/scenarios/executors/ramping-vus/#get-the-stage-index
import { getCurrentStageIndex } from 'https://jslib.k6.io/k6-utils/1.3.0/index.js';
import { check, sleep } from 'k6';
import http from 'k6/http';
import { handleSummary as handleSummary_helper } from './handleSummary.js';
import { getHeader, getPayload, login } from './utils.js';

const baseUrl = __ENV.BASE_URL;

const STAGES = [
    {
        duration: '20s',
        target: 100,
    },
    {
        duration: '100s',
        target: 100,
        is_target: true,
    },
    {
        duration: '20s',
        target: 0,
    },
]

const getUri = (data) => `/trip/${data.tripId}/reservation/analysis/text`

export const options = {
    executor: 'ramping-arrival-rate',
    startRate: 0,
    timeUnit: '1s',
    preAllocatedVUs: 100,
    maxVUs: 100,
    stages: STAGES,
    thresholds: {
        http_req_duration: ['p(95)<10000'],
        http_req_failed: ['rate<0.05'],
    },
};

export function setup() {
    return login()
}

export default (data) => {
    const stage = getCurrentStageIndex();

    const uri = `/trip/${data.tripId}/reservation/analysis/text`;
    const url = `${baseUrl}${uri}`;
    const payload = getPayload(__VU)

    console.log(`stage: ${stage}`)
    console.log(`header:\n${JSON.stringify(getHeader(stage))}`)

    const params = {
        headers: {
            ...getHeader(stage),
            'Content-Type': 'application/json',
        },
    };

    const urlRes = http.post(url, JSON.stringify(payload), params);
    check(urlRes, { 'status returned 202': (r) => r.status == 202 });
    sleep(1);
    // MORE STEPS
    // Here you can have more steps or complex script
    // Step1
    // Step2
    // etc.
};

export function handleSummary(data) {
    return handleSummary_helper(data, STAGES, getUri)
}