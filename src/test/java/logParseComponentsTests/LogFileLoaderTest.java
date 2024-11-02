package logParseComponentsTests;

import backend.academy.exceptions.LogParseException;
import backend.academy.logParseComponents.LogFileLoader;
import dataForTesting.TestDataProvider;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class LogFileLoaderTest {
    @ParameterizedTest
    @ValueSource(strings = {
        TestDataProvider.SAMPLE_URL,
        TestDataProvider.SAMPLE_FILE
    })
    @Timeout(value = 10)
    void testValidLoad(String fileOrUrl) {
        try {
            List<String> logs = LogFileLoader.loadLogs(fileOrUrl);

            Assertions.assertFalse(logs.isEmpty(), "Logs should not be empty");
        } catch (IOException e) {
            throw new RuntimeException("Test didn't pass due to IO error", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Invalid file",
        "https://invalidurl"
    })
    void testInvalidLoad(String fileOrUrl) {
        Assertions.assertThrows(LogParseException.class, () -> LogFileLoader.loadLogs(fileOrUrl));
    }
}
