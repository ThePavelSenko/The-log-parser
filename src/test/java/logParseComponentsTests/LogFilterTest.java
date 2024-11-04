package logParseComponentsTests;

import backend.academy.logParseComponents.LogFileLoader;
import backend.academy.logParseComponents.LogFilter;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static dataForTesting.TestDataProvider.SAMPLE_FILE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class LogFilterTest {
    private static List<String> logsBeforeFilter;

    @BeforeAll
    static void setUp() throws IOException {
        logsBeforeFilter = LogFileLoader.loadLogs(SAMPLE_FILE);
    }

    @ParameterizedTest
    @CsvSource({
        "agent, Debian",
        "request, GET"
    })
    void testValidFilteredAllLogs(String field, String value) {
        List<String> logsAfterFilter = LogFilter.sortLogsByInputFields(logsBeforeFilter, field, value);
        assertThat(logsAfterFilter.size()).isEqualTo(4);
    }

    @ParameterizedTest
    @CsvSource({
        "iP, 212.77.185.81",
        "agent, DeBian APT-HTTP/1.3 (0.8.10",
    })
    void testValidFilteredOneLogDate(String field, String value) {
        List<String> logsAfterFilter = LogFilter.sortLogsByInputFields(logsBeforeFilter, field, value);
        assertThat(logsAfterFilter.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ip", "request", "code", "agent"})
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
