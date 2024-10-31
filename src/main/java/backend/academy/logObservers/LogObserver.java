package backend.academy.logObservers;

import backend.academy.logParseComponents.LogReport;

public interface LogObserver {
    void update(LogReport log);
}
