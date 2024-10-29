package backend.academy.logObservers;

import backend.academy.LogReport;

public interface LogObserver {
    void update(LogReport log);
}
