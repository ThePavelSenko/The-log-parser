package backend.academy.logObservers;

import backend.academy.logParseComponents.LogReport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Observer to calculate the 95th percentile of response sizes.
 */
@Log4j2
public class ResponseSizePercentileObserver implements LogObserver {

    private final List<Long> responseSizePercentiles = new ArrayList<>();

    private static final double PERCENTILE = 0.95;

    private boolean isSorted = false;

    @Getter
    private double percentile95 = 0;

    @Override
    public void update(LogReport logger) {
        try {
            long responseSize = Long.parseLong(logger.responseSize());
            responseSizePercentiles.add(responseSize);
            isSorted = false; // Mark the data as unsorted after adding a new entry
            percentile95 = calculatePercentile95();
        } catch (NumberFormatException e) {
            log.error("Invalid response size: {}", logger.responseSize(), e);
        }
    }

    private double calculatePercentile95() {
        if (responseSizePercentiles.isEmpty()) {
            return 0;
        }

        if (!isSorted) {
            Collections.sort(responseSizePercentiles);
            isSorted = true; // Mark the data as sorted
        }

        int index = (int) Math.ceil(PERCENTILE * responseSizePercentiles.size()) - 1;
        return responseSizePercentiles.get(index);
    }
}
