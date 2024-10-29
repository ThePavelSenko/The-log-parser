package backend.academy.logObservers;

import backend.academy.*;
import lombok.*;
import java.util.*;

@Getter
public class RecourseRequestsObserver implements LogObserver {
    private final Map<String, Integer> resourceRequests = new HashMap<>();

    @Override
    public void update(LogReport log) {
        resourceRequests.put(log.referrer(), resourceRequests.getOrDefault(log.referrer(), 0) + 1);
    }
}
