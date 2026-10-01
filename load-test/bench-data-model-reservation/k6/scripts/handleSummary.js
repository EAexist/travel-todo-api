import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.2/index.js';

function parseDurationToMs(durationStr) {
    if (typeof durationStr !== 'string') {
        durationStr = String(durationStr);
    }

    const str = durationStr.trim().toLowerCase();
    if (!str) return 0;

    // Match all number + unit pairs globally (e.g., "1m", "0.5s", "500ms")
    const regex = /(\d+(?:\.\d+)?)\s*(ms|s|m|h)/g;
    let match;
    let totalMs = 0;
    let matchedCount = 0;

    while ((match = regex.exec(str)) !== null) {
        matchedCount++;
        const value = parseFloat(match[1]);
        const unit = match[2];

        switch (unit) {
            case 'ms': totalMs += value; break;
            case 's': totalMs += value * 1000; break;
            case 'm': totalMs += value * 60 * 1000; break;
            case 'h': totalMs += value * 60 * 60 * 1000; break;
        }
    }

    // Fallback: If no explicit unit was provided (e.g., "5"), treat as seconds
    if (matchedCount === 0) {
        const plainNum = parseFloat(str);
        return isNaN(plainNum) ? 0 : plainNum * 1000;
    }

    return totalMs;
}
function calculateIterationTimestamps(scenarios, testStartTime) {
    const baseTime = new Date(testStartTime).getTime();
    const scenarioList = Array.isArray(scenarios) ? scenarios : Object.values(scenarios);

    return scenarioList.map((scenario, index) => {
        const durationMs = parseDurationToMs(scenario.duration);
        const startTimeOffsetMs = parseDurationToMs(scenario.startTime || '0s');

        const iterationStart = new Date(baseTime + startTimeOffsetMs);
        const iterationEnd = new Date(baseTime + startTimeOffsetMs + durationMs);

        const isTarget = scenario.tags?.is_target === 'true';

        return {
            iteration: index,
            duration: scenario.duration,
            target_vus: scenario.vus ?? scenario.target,
            start_time: iterationStart.toISOString(),
            end_time: iterationEnd.toISOString(),
            is_target: isTarget,
        };
    });
}

export function handleSummary(data, scenarios, getUri) {
    const summaryPath = __ENV.SUMMARY_PATH
    const totalDurationMs = data?.state.testRunDurationMs;
    const testEndTime = new Date();
    const testStartTime = new Date(testEndTime.getTime() - totalDurationMs);

    const iterationTimeline = calculateIterationTimestamps(scenarios, testStartTime);
    const summaryManifest = {
        uri: getUri(data),
        iterations: iterationTimeline,
    };

    return {
        [summaryPath]: JSON.stringify(summaryManifest, null, 2),
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    };
}