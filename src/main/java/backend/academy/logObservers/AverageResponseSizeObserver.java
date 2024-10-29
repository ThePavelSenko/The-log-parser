package backend.academy.logObservers;

import backend.academy.*;
import lombok.*;

public class AverageResponseSizeObserver implements LogObserver {
    private @Getter long averageResponseSize = 0;
    private long totalResponseSize = 0;
    private long totalRequests = 0;

    @Override
    public void update(LogReport log) {
        totalRequests++;
        totalResponseSize += Long.parseLong(log.responseSize());
        averageResponseSize = totalResponseSize / totalRequests;
    }
}
