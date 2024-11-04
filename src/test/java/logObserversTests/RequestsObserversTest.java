package logObserversTests;

import backend.academy.logObservers.RequestsObservers;
import backend.academy.logParseComponents.LogReport;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestsObserversTest {

    @Test
    void testUpdateWithNewRequest() {
        RequestsObservers observer = new RequestsObservers();
        LogReport logMock = mock(LogReport.class);
        when(logMock.request()).thenReturn("GET /index");

        observer.update(logMock);

        assertEquals(1, observer.requests().get("GET /index"));
    }

    @Test
    void testUpdateWithExistingRequest() {
        RequestsObservers observer = new RequestsObservers();
        LogReport logMock = mock(LogReport.class);
        when(logMock.request()).thenReturn("POST /submit");

        observer.update(logMock);
        observer.update(logMock);

        assertEquals(2, observer.requests().get("POST /submit"));
    }
}

