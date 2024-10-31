package backend.academy.logObservers;

import backend.academy.logParseComponents.LogReport;
import java.util.*;
import lombok.Getter;

@Getter
public class CodeStatusesObserver implements LogObserver {
    private final Map<String, Integer> codeStatuses = new HashMap<>();

    @Override
    public void update(LogReport log) {
        String statusCode = log.httpStatusCode();
        codeStatuses.merge(statusCode, 1, Integer::sum);
    }
}
