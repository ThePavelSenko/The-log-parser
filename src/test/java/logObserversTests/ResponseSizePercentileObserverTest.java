package logObserversTests;

import backend.academy.logObservers.ResponseSizePercentileObserver;
import backend.academy.logParseComponents.LogReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResponseSizePercentileObserverTest {

    private ResponseSizePercentileObserver observer;

    @BeforeEach
    void setUp() {
        observer = new ResponseSizePercentileObserver();
    }

    @Test
    void shouldCalculatePercentile95WithSingleResponseSize() {
        LogReport logReport = mock(LogReport.class);
        when(logReport.responseSize()).thenReturn("100");



        observer.update(logReport);

        assertThat(observer.percentile95()).isEqualTo(100);
    }

    @Test
    void shouldCalculatePercentile95WithMultipleResponseSizes() {
        LogReport logReport1 = mock(LogReport.class);
        LogReport logReport2 = mock(LogReport.class);
        LogReport logReport3 = mock(LogReport.class);

        when(logReport1.responseSize()).thenReturn("100");
        when(logReport2.responseSize()).thenReturn("200");
        when(logReport3.responseSize()).thenReturn("300");

        observer.update(logReport1);
        observer.update(logReport2);
        observer.update(logReport3);

        // Expected 95th percentile is 300 because it's closest to the top 5% in a sorted [100, 200, 300]
        assertThat(observer.percentile95()).isEqualTo(300);
    }

    @Test
    void shouldHandleInvalidResponseSize() {
        LogReport logReport = mock(LogReport.class);
        when(logReport.responseSize()).thenReturn("invalid");

        observer.update(logReport);

        // Since "invalid" responseSize is ignored, percentile should remain at its default or previous value
        assertThat(observer.percentile95()).isEqualTo(0);
    }

    @Test
    void shouldCalculatePercentile95WithEmptyList() {
        // If no response sizes are added, percentile95 should be 0 by default
        assertThat(observer.percentile95()).isEqualTo(0);
    }
}
