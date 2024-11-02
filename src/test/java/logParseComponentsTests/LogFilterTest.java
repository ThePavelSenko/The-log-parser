package logParseComponentsTests;

import backend.academy.logParseComponents.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import static dataForTesting.TestDataProvider.*;
import static org.assertj.core.api.Assertions.*;

public class LogFilterTest {
    private static List<String> logsBeforeFilter;;

    @BeforeAll
    static void setUp() throws IOException {
        logsBeforeFilter = LogFileLoader.loadLogs(SAMPLE_FILE);
    }

    @ParameterizedTest
    @CsvSource({
        "user agent, Debian",
        "request, GET"
    })
    void testValidFilteredAllLogs(String field, String value) {
        List<String> logsAfterFilter = LogFilter.sortLogsByInputFields(logsBeforeFilter, field, value);

        assertThat(logsAfterFilter.size()).isEqualTo(4);
    }

    @ParameterizedTest
    @CsvSource({
        "ip addrEss, 212.77.185.81",
        "user agent, DeBian APT-HTTP/1.3 (0.8.10",
    })
    void testValidFilteredOneLogDate(String field, String value) {
        List<String> logsAfterFilter = LogFilter.sortLogsByInputFields(logsBeforeFilter, field, value);

        assertThat(logsAfterFilter.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ip address", "request", "status code", "user agent"})
    void testCorrectFilteredZeroLogDates(String field) {
        List<String> logsAfterFilter =
            LogFilter.sortLogsByInputFields(logsBeforeFilter, field, "Every not found input");

        assertThat(logsAfterFilter.size()).isEqualTo(0);
    }

    @Test
    void testInvalidInput() {
        assertThatThrownBy(() ->
            LogFilter.sortLogsByInputFields(logsBeforeFilter, "incorrect input", "Every input"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
