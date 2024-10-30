package logParseComponentsTests;

import backend.academy.logParseComponents.LogFilter;
import backend.academy.logParseComponents.LogParser;
import backend.academy.logParseComponents.LogReport;
import dataForTesting.TestDataProvider;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class LogFilterTest {
    List<LogReport> logList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        LogReport earlyLogReport = LogParser.parseLog(TestDataProvider.SAMPLE_EARLY_LOG);

        LogReport lateLogReport = LogParser.parseLog(TestDataProvider.SAMPLE_LATE_LOG);

        logList.addAll(List.of(lateLogReport, earlyLogReport));
    }

    @Test
    public void testFilterCase1() {
        LocalDateTime startTime = LocalDateTime.parse("17/May/2015:14:00:00 +0000", LogFilter.DATE_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse("17/May/2015:16:00:00 +0000", LogFilter.DATE_FORMATTER);

        List<LogReport> sortedLogs = LogFilter.filterAndSortLogsByTimeRange(logList, startTime, endTime);

        assertThat(sortedLogs.getFirst().timestamp()).isEqualTo("17/May/2015:14:05:39 +0000");
        assertThat(sortedLogs.getLast().timestamp()).isEqualTo("17/May/2015:15:05:01 +0000");
    }

    @Test
    public void testFilterCase2() {
        LocalDateTime startTime = LocalDateTime.parse("17/May/2015:14:00:00 +0000", LogFilter.DATE_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse("17/May/2015:15:00:00 +0000", LogFilter.DATE_FORMATTER);

        List<LogReport> sortedLogs = LogFilter.filterAndSortLogsByTimeRange(logList, startTime, endTime);

        assertThat(sortedLogs.size()).isEqualTo(1);
    }

}
