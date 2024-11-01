package backend.academy.logObservers;

import backend.academy.logParseComponents.LogReport;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class RequestsObservers implements LogObserver {
    private final Map<String, Integer> requests = new LinkedHashMap<>();

    @Override
    public void update(LogReport log) {
        requests.merge(log.request(), 1, Integer::sum);
    }
}
