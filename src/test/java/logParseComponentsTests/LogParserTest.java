package logParseComponentsTests;

import backend.academy.exceptions.LogParseException;
import backend.academy.logParseComponents.LogReport;
import backend.academy.logParseComponents.LogParser;
import dataForTesting.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;

public class LogParserTest {

    @Test
    public void testValidLogParse() {
        LogReport logReport = LogParser.parseLog(TestDataProvider.SAMPLE_EARLY_LOG);


        assertThat(logReport).isNotNull();

        assertThat(logReport.ipAddress()).isEqualTo("91.239.186.133");
        assertThat(logReport.timestamp()).isEqualTo("17/May/2015:14:05:39 +0000");
        assertThat(logReport.request()).isEqualTo("GET /downloads/product_2 HTTP/1.1");
        assertThat(logReport.httpStatusCode()).isEqualTo("304");
        assertThat(logReport.responseSize()).isEqualTo("1234");
        assertThat(logReport.referrer()).isEqualTo("-");
        assertThat(logReport.userAgent()).isEqualTo("Debian APT-HTTP/1.3 (0.9.7.9)");
    }

    @Test
    public void testInvalidLogParse() {
        String emptyLog = "";
        String invalidLog = "Invalid log";

        Assertions.assertThrows(LogParseException.class, () -> {
            LogReport logReport = LogParser.parseLog(invalidLog);
        });
        Assertions.assertThrows(LogParseException.class, () -> {
            LogReport logReport = LogParser.parseLog(emptyLog);
        });
    }
}
