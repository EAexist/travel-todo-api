// https://grafana.com/docs/k6/latest/testing-guides/test-types/smoke-testing/#smoke-testing-in-k6
// https://grafana.com/docs/k6/latest/using-k6/scenarios/executors/ramping-target_rps/#get-the-stage-index
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.2/index.js';
import { getCurrentStageIndex } from 'https://jslib.k6.io/k6-utils/1.3.0/index.js';
import { check } from 'k6';
import http from 'k6/http';
import { handleSummary as handleSummary_helper } from './handleSummary.js';
import { getHeader, getPayload, login } from './utils.js';

const baseUrl = __ENV.BASE_URL;

const STAGES = [
    { duration: '40s', target: 50, is_target: "true" },
    { duration: '40s', target: 300, is_target: "true" },
]

const getUri = (data) => `/trip/${data.tripId}/reservation/analysis/text`

export const options = {
    scenarios: {
        smoke: {
            executor: 'ramping-arrival-rate',
            startRate: 0,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 4500,
            stages: STAGES.map(({ duration, target }) => ({ duration, target })),
        },
    },
    thresholds: {
        'http_req_duration{stage:0}': [{ threshold: 'p(95)<10000', abortOnFail: true }],
        'http_req_failed{stage:0}': [{ threshold: 'rate<0.05', abortOnFail: true }],
        'http_req_duration{stage:1}': [{ threshold: 'p(95)<10000', abortOnFail: true }],
        'http_req_failed{stage:1}': [{ threshold: 'rate<0.05', abortOnFail: true }],
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

    const params = {
        headers: {
            ...getHeader(stage),
            'Content-Type': 'application/json',
        },
        tags: {
            stage: `${stage}`,
        },
        responseType: 'none',
    };

    const urlRes = http.post(url, JSON.stringify(payload), params);
    check(urlRes, {
        'status is 201 or 202': (r) => [201, 202].includes(r.status)
    });
};

export function handleSummary(data) {
    return handleSummary_helper(data, STAGES, getUri, textSummary)
}