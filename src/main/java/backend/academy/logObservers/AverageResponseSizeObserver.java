package backend.academy.logObservers;

import backend.academy.logParseComponents.LogReport;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AverageResponseSizeObserver implements LogObserver {
    private @Getter long averageResponseSize = 0;
    private long totalResponseSize = 0;
    private long totalRequests = 0;

    @Override
    public void update(LogReport logger) {
        try {
            long responseSize = Long.parseLong(logger.responseSize());
            totalResponseSize += responseSize;
            totalRequests++;  // Only increment if the response size is valid

            // Update the average response size, ensuring no division by zero
            averageResponseSize = totalRequests > 0 ? totalResponseSize / totalRequests : 0;
        } catch (NumberFormatException e) {
            // Log the error but don't alter totalRequests or average
            log.error("Invalid response size: {}", logger.responseSize(), e);
        }
    }
}

