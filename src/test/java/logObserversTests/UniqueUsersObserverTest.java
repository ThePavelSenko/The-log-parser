package logObserversTests;

import backend.academy.logObservers.UniqueUsersObserver;
import backend.academy.logParseComponents.LogReport;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UniqueUsersObserverTest {

    @Test
    void testUpdateCountsAndSortsUsers() {
        UniqueUsersObserver observer = new UniqueUsersObserver();
        LogReport log1 = mock(LogReport.class);
        LogReport log2 = mock(LogReport.class);
        LogReport log3 = mock(LogReport.class);

        when(log1.ipAddress()).thenReturn("192.168.1.1");
        when(log2.ipAddress()).thenReturn("192.168.1.2");
        when(log3.ipAddress()).thenReturn("192.168.1.1");

        observer.update(log1);
        observer.update(log2);
        observer.update(log3);

        Map<String, Integer> users = observer.users();

        assertEquals(2, users.size());
        assertEquals(2, users.get("192.168.1.1"));
        assertEquals(1, users.get("192.168.1.2"));

        String firstKey = users.keySet().iterator().next();
        assertEquals("192.168.1.1", firstKey);
    }
}

