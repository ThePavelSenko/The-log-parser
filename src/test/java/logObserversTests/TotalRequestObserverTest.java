package logObserversTests;

import backend.academy.logObservers.TotalRequestObserver;
import backend.academy.logParseComponents.LogReport;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class TotalRequestObserverTest {

    @Test
    void testUpdateIncrementsTotalRequests() {
        TotalRequestObserver observer = new TotalRequestObserver();
        LogReport logMock = mock(LogReport.class);

        observer.update(logMock);
        observer.update(logMock);

        assertEquals(2, observer.totalRequests());
    }
}

