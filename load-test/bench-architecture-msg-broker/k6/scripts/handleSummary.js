
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

        return {
            stage_index: index,
            stage_id: `stage_${index}`,
            durationSeconds: durationMs / 1000,
            target_rps: stage.target,
            start_time: stageStart.toISOString(),
            end_time: stageEnd.toISOString(),
            is_target: stage.is_target === "true",
        };
    });
}

function handleSummary(data, stages, getUri, textSummaryFn) {
    const summaryPath = __ENV.SUMMARY_PATH;
    const totalDurationMs = data?.state?.testRunDurationMs || 0;
    const testEndTime = new Date();
    const testStartTime = new Date(testEndTime.getTime() - totalDurationMs);

    const stageTimeline = calculateStageTimestamps(stages, testStartTime);

    let foundFailure = false;

    const decoratedTimeline = stageTimeline.map((stage) => {
        const stageIdx = stage.stage_index;
        const failedMetric = `http_req_failed{stage:${stageIdx}}`;
        const durationMetric = `http_req_duration{stage:${stageIdx}}`;
        const droppedMetric = `dropped_iterations{stage:${stageIdx}}`;

        let stageFailed = false;
        let hasConfiguredThreshold = false;

        // Inspect both metric thresholds for this stage
        [failedMetric, durationMetric, droppedMetric].forEach((metricName) => {
            const metric = data?.metrics?.[metricName];
            if (metric && metric.thresholds) {
                hasConfiguredThreshold = true;
                Object.values(metric.thresholds).forEach((threshold) => {
                    // k6 sets `ok: false` when a threshold fails
                    if (threshold.ok === false) {
                        stageFailed = true;
                    }
                });
            }
        });

        // Determine if stage was skipped due to an earlier abort
        const skipped = foundFailure;

        if (stageFailed) {
            foundFailure = true;
        }

        return {
            ...stage,
            failed: stageFailed,
            skipped: skipped,
        };
    });

    const summaryManifest = {
        uri: getUri ? getUri(data) : '',
        stages: decoratedTimeline,
    };

    const summaryText = textSummaryFn
        ? textSummaryFn(data, { indent: ' ', enableColors: true })
        : 'Summary placeholder';

    return {
        [summaryPath]: JSON.stringify(summaryManifest, null, 2),
        'stdout': summaryText,
    };
}

export { calculateStageTimestamps, handleSummary, parseDurationToMs };
