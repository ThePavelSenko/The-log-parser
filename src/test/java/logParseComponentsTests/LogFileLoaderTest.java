package logParseComponentsTests;

import backend.academy.logParseComponents.*;
import dataForTesting.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.io.*;
import java.util.*;

public class LogFileLoaderTest {
    @ParameterizedTest
    @ValueSource(strings = {
        TestDataProvider.SAMPLE_URL,
        TestDataProvider.SAMPLE_FILE
    })
    @Timeout(value = 10)
    public void testValidLoad(String fileOrUrl) {
        try {
            List<String> logs = LogFileLoader.loadLogs(fileOrUrl);

            Assertions.assertFalse(logs.isEmpty(), "Logs should not be empty");
        } catch (IOException e) {
            throw new RuntimeException("Test didn't pass due to IO error", e);
        }
    }
}
