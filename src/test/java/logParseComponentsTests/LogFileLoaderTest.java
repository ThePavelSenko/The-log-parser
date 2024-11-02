package logParseComponentsTests;

import backend.academy.logParseComponents.LogFileLoader;
import dataForTesting.TestDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.util.List;

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
}
