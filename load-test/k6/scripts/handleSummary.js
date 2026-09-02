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

function calculateStageTimestamps(stages, testStartTime) {
    let accumulatedTimeMs = 0;
    const baseTime = new Date(testStartTime).getTime();

    return stages.map((stage, index) => {

        const durationMs = parseDurationToMs(stage.duration);

        const stageStart = new Date(baseTime + accumulatedTimeMs);
        accumulatedTimeMs += durationMs;
        const stageEnd = new Date(baseTime + accumulatedTimeMs);

        console.log(stage.tags, stage.is_target)

        return {
            stage_index: index,
            stage_id: `stage_${index}`,
            duration: stage.duration,
            target_vus: stage.target,
            start_time: stageStart.toISOString(),
            end_time: stageEnd.toISOString(),
            is_target: stage.is_target ?? false,
        };
    });
}

export function handleSummary(data, stages, getUri) {
    const summaryPath = __ENV.SUMMARY_PATH
    const totalDurationMs = data?.state.testRunDurationMs;
    const testEndTime = new Date();
    const testStartTime = new Date(testEndTime.getTime() - totalDurationMs);

    const stageTimeline = calculateStageTimestamps(stages, testStartTime);
    const summaryManifest = {
        uri: getUri(data),
        stages: stageTimeline,
    };

    return {
        [summaryPath]: JSON.stringify(summaryManifest, null, 2),
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    };
}