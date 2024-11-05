package logParseComponentsTests;

import backend.academy.logParseComponents.LogFileLoader;
import backend.academy.logParseComponents.LogFilter;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Load logs from sample file")
    static void setUp() throws IOException {
        // Loads logs from a sample file to use in tests
        logsBeforeFilter = LogFileLoader.loadLogs(SAMPLE_FILE);
    }

    @ParameterizedTest
    @CsvSource({
        "agent, Debian",
        "request, GET"
    })
    @DisplayName("Filter logs by valid fields")
    void testValidFilteredAllLogs(String field, String value) {
        // Filters logs by a valid field and checks if the filtered count matches the expected size
        List<String> logsAfterFilter = LogFilter.sortLogsByInputFields(logsBeforeFilter, field, value);
        assertThat(logsAfterFilter.size()).isEqualTo(4);
    }

    @ParameterizedTest
    @CsvSource({
        "iP, 212.77.185.81",
        "agent, DeBian APT-HTTP/1.3 (0.8.10"
    })
    @DisplayName("Filter logs with one matching entry")
    void testValidFilteredOneLogDate(String field, String value) {
        // Filters logs by a specific field with only one match and verifies the count
        List<String> logsAfterFilter = LogFilter.sortLogsByInputFields(logsBeforeFilter, field, value);
        assertThat(logsAfterFilter.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ip", "request", "code", "agent"})
    @DisplayName("Filter logs with no matching entries")
    void testCorrectFilteredZeroLogDates(String field) {
        // Attempts to filter logs by an unmatched field value and checks if the result is empty
        List<String> logsAfterFilter =
            LogFilter.sortLogsByInputFields(logsBeforeFilter, field, "Every not found input");
        assertThat(logsAfterFilter.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Invalid input throws IllegalArgumentException")
    void testInvalidInput() {
        // Tests if an invalid field input throws the expected exception
        assertThatThrownBy(() ->
            LogFilter.sortLogsByInputFields(logsBeforeFilter, "incorrect input", "Every input"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
