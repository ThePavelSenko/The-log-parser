package logParseComponentsTests;

import backend.academy.logParseComponents.LogFileLoader;
import backend.academy.logParseComponents.LogParser;
import backend.academy.logParseComponents.LogReportFormatter;
import backend.academy.logObservers.CodeStatusesObserver;
import backend.academy.logObservers.TotalRequestObserver;
import dataForTesting.TestDataProvider;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LogReportFormatterTest {
    private String report;

    @BeforeEach
    void setUp() {
        LogParser.addObserver(new TotalRequestObserver());
        LogParser.addObserver(new CodeStatusesObserver());

        try {
            List<String> logLines = LogFileLoader.loadLogs(TestDataProvider.SAMPLE_FILE);
            String fileName = "TestLogFile";
            LogReportFormatter formatter = new LogReportFormatter(fileName, LogParser.observers());

            for (String line : logLines) {
                LogParser.parseLog(line);
            }

            report = formatter.generateAdocReport();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testFormatter() {
        assertThat(report).contains("TotalRequest - total Requests").contains("4");
        assertThat(report).contains("CodeStatuses - code Statuses")
            .contains("404")
            .contains("3")
            .contains("206")
            .contains("1");
    }
}
