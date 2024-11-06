package logObserversTests;

import backend.academy.logObservers.CodeStatusesObserver;
import backend.academy.logParseComponents.LogReport;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CodeStatusesObserverTest {

    @Test
    void testUpdateWithNewStatusCode() {
        CodeStatusesObserver observer = new CodeStatusesObserver();
        LogReport logMock = mock(LogReport.class);
        when(logMock.httpStatusCode()).thenReturn("200");

        observer.update(logMock);

        assertEquals(1, observer.codeStatuses().get("200"));
    }

    @Test
    void testUpdateWithExistingStatusCode() {
        CodeStatusesObserver observer = new CodeStatusesObserver();
        LogReport logMock = mock(LogReport.class);
        when(logMock.httpStatusCode()).thenReturn("404");

        observer.update(logMock);
        observer.update(logMock);

        assertEquals(2, observer.codeStatuses().get("404"));
    }
}

