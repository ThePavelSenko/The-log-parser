package backend.academy.logObservers;

import backend.academy.logParseComponents.LogReport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class UniqueUsersObserver implements LogObserver {
    private Map<String, Integer> users = new LinkedHashMap<>();

    @Override
    public void update(LogReport log) {
        users.merge(log.ipAddress(), 1, Integer::sum);

        users = users.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
}
