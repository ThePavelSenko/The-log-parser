package logParseComponentsTests;

import backend.academy.logObservers.CodeStatusesObserver;
import backend.academy.logObservers.TotalRequestObserver;
import backend.academy.logParseComponents.LogFileLoader;
import backend.academy.logParseComponents.LogParser;
import backend.academy.logParseComponents.LogReportFormatter;
import dataForTesting.TestDataProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

class LogReportFormatterTest {
    private static String report;

    @BeforeAll
    static void setUp() {
        LogParser.addObserver(new TotalRequestObserver());
        LogParser.addObserver(new CodeStatusesObserver());

        try {
            List<String> logLines = LogFileLoader.loadLogs(TestDataProvider.SAMPLE_FILE);
            String fileName = "TestLogFile";
            LogReportFormatter formatter = new LogReportFormatter(fileName, LogParser.observers());

            for (String line : logLines) {
                LogParser.parseLog(line);
            }

            report = formatter.generateAdocReport(); // Initialize the report
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testValueFormatter() {
        assertThat(report).contains("TotalRequest - total Requests").contains("4");
    }

    @Test
    void testMapFormatter() {
        assertThat(report).contains("CodeStatuses - code Statuses")
            .contains("404")
            .contains("3")
            .contains("206")
            .contains("1");
    }
}
