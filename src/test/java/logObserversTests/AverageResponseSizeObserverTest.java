package logObserversTests;

import backend.academy.logParseComponents.*;
import dataForTesting.TestDataProvider;
import backend.academy.logObservers.AverageResponseSizeObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AverageResponseSizeObserverTest {
    private AverageResponseSizeObserver observer;

    @BeforeEach
    void setUp() {
        observer = new AverageResponseSizeObserver();
    }

    @Test
    void testAverageResponseSizeSingleLog() {
        LogReport log1 = LogParser.parseLog(TestDataProvider.SAMPLE_EARLY_LOG);
        observer.update(log1);

        assertEquals(1234, observer.averageResponseSize());
    }

    @Test
    void testAverageResponseSizeMultipleLogs() {
        LogReport log1 = LogParser.parseLog(TestDataProvider.SAMPLE_EARLY_LOG);
        LogReport log2 = LogParser.parseLog(TestDataProvider.SAMPLE_LATE_LOG);

        observer.update(log1);
        observer.update(log2);

        long expectedAverage = (1234 + 777) / 2;
        assertEquals(expectedAverage, observer.averageResponseSize());
    }

    @Test
    void testAverageResponseSizeNoLogs() {
        assertEquals(0, observer.averageResponseSize());
    }
}
