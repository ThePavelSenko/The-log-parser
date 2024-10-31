package backend.academy.logObservers;

import backend.academy.logParseComponents.LogReport;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class RecourseRequestsObserver implements LogObserver {
    private final Map<String, Integer> resourceRequests = new HashMap<>();

    @Override
    public void update(LogReport log) {
        resourceRequests.merge(log.referrer(), 1, Integer::sum);
    }
}
