package logObserversTests;

import backend.academy.logObservers.RecourseRequestsObserver;
import backend.academy.logParseComponents.LogReport;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecourseRequestsObserverTest {

    @Test
    void testUpdateWithNewReferrer() {
        RecourseRequestsObserver observer = new RecourseRequestsObserver();
        LogReport logMock = mock(LogReport.class);
        when(logMock.referrer()).thenReturn("example.com");

        observer.update(logMock);

        assertEquals(1, observer.resourceRequests().get("example.com"));
    }

    @Test
    void testUpdateWithExistingReferrer() {
        RecourseRequestsObserver observer = new RecourseRequestsObserver();
        LogReport logMock = mock(LogReport.class);
        when(logMock.referrer()).thenReturn("example.com");

        observer.update(logMock);
        observer.update(logMock);

        assertEquals(2, observer.resourceRequests().get("example.com"));
    }
}

