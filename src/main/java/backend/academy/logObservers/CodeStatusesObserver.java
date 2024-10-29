package backend.academy.logObservers;

import backend.academy.*;
import lombok.*;
import java.util.*;

@Getter
public class CodeStatusesObserver implements LogObserver {
    private final Map<String, Integer> codeStatuses = new HashMap<>();

    @Override
    public void update(LogReport log) {
        codeStatuses.put(log.httpStatusCode(), codeStatuses.getOrDefault(log.httpStatusCode(), 0) + 1);
    }
}
