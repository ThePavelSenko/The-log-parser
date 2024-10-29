package backend.academy.logObservers;

import backend.academy.*;
import lombok.*;

@Getter
public class TotalRequestObserver implements LogObserver {
    private long totalRequests = 0;

    @Override
    public void update(LogReport log) {
        totalRequests++;
    }
}

