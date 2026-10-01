import assert from 'node:assert/strict';
import test from 'node:test';
import { calculateStageTimestamps, handleSummary, parseDurationToMs } from './handleSummary.js';

test('parseDurationToMs - basic single unit durations', () => {
    assert.equal(parseDurationToMs('500ms'), 500);
    assert.equal(parseDurationToMs('10s'), 10000);
    assert.equal(parseDurationToMs('1.5s'), 1500);
    assert.equal(parseDurationToMs('2m'), 120000);
    assert.equal(parseDurationToMs('1h'), 3600000);
});

test('parseDurationToMs - fallback without explicit units (defaults to seconds)', () => {
    assert.equal(parseDurationToMs('5'), 5000);
    assert.equal(parseDurationToMs(10), 10000);
    assert.equal(parseDurationToMs(''), 0);
    assert.equal(parseDurationToMs('invalid'), 0);
});

test('parseDurationToMs - combined multi-unit durations', () => {
    assert.equal(parseDurationToMs('1m30s'), 90000);
    assert.equal(parseDurationToMs('1h 15m 30s 500ms'), 4530500);
});

test('calculateStageTimestamps - accumulates timeline across stages', () => {
    const startTime = '2026-09-12T00:00:00.000Z';
    const stages = [
        { duration: '10s', target: 50, is_target: 'true' },
        { duration: '30s', target: 100, is_target: 'false' },
        { duration: '1m', target: 0 },
    ];

    const result = calculateStageTimestamps(stages, startTime);

    assert.equal(result.length, 3);

    // Stage 0: 0s -> 10s
    assert.deepEqual(result[0], {
        stage_index: 0,
        stage_id: 'stage_0',
        durationSeconds: 10,
        target_rps: 50,
        start_time: '2026-09-12T00:00:00.000Z',
        end_time: '2026-09-12T00:00:10.000Z',
        is_target: true,
    });

    // Stage 1: 10s -> 40s (10s + 30s)
    assert.deepEqual(result[1], {
        stage_index: 1,
        stage_id: 'stage_1',
        durationSeconds: 30,
        target_rps: 100,
        start_time: '2026-09-12T00:00:10.000Z',
        end_time: '2026-09-12T00:00:40.000Z',
        is_target: false,
    });

    // Stage 2: 40s -> 1m40s (40s + 60s)
    assert.deepEqual(result[2], {
        stage_index: 2,
        stage_id: 'stage_2',
        durationSeconds: 60,
        target_rps: 0,
        start_time: '2026-09-12T00:00:40.000Z',
        end_time: '2026-09-12T00:01:40.000Z',
        is_target: false,
    });
});

test('handleSummary correctly marks failed and skipped stages', () => {
    global.__ENV = { SUMMARY_PATH: 'summary.json' };

    const stages = [
        { duration: '10s', target: 100 }, // Stage 0
        { duration: '10s', target: 200 }, // Stage 1 (Fails)
        { duration: '10s', target: 300 }, // Stage 2 (Skipped)
    ];

    const mockData = {
        state: { testRunDurationMs: 20000 },
        metrics: {
            'http_req_duration{stage:1}': {
                thresholds: {
                    'p(95)<10000': { ok: false }, // Failed
                },
            },
            'http_req_failed{stage:1}': {
                thresholds: {
                    'rate<0.05': { ok: true },
                },
            },
        },
    };

    const res = handleSummary(mockData, stages, () => 'http://localhost', null);
    const manifest = JSON.parse(res['summary.json']);

    // Stage 0: Passed
    assert.equal(manifest.stages[0].failed, false);
    assert.equal(manifest.stages[0].skipped, false);

    // Stage 1: Failed
    assert.equal(manifest.stages[1].failed, true);
    assert.equal(manifest.stages[1].skipped, false);

    // Stage 2: Skipped due to prior failure
    assert.equal(manifest.stages[2].failed, false);
    assert.equal(manifest.stages[2].skipped, true);
});