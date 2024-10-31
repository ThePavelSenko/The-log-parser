package backend.academy.logObservers;

import backend.academy.logParseComponents.LogReport;
import lombok.Getter;

@Getter
public class TotalRequestObserver implements LogObserver {
    private long totalRequests = 0;

    @Override
    public void update(LogReport log) {
        totalRequests++;
    }
}

