package logParseComponentsTests;

import backend.academy.logObservers.CodeStatusesObserver;
import backend.academy.logObservers.TotalRequestObserver;
import backend.academy.logParseComponents.LogFileLoader;
import backend.academy.logParseComponents.LogParser;
import backend.academy.logParseComponents.LogReportFormatter;
import dataForTesting.TestDataProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.assertThat;

class LogReportFormatterTest {
    private static final String BASE_FILE_NAME = "TestLogFile";

    // Temporary directory for storing report files
    @TempDir
    private Path tempDir;

    private Path adocReportPath;
    private Path mdReportPath;

    @BeforeAll
    static void setUpObservers() {
        // Add observers to LogParser once before all tests
        LogParser.addObserver(new TotalRequestObserver());
        LogParser.addObserver(new CodeStatusesObserver());
    }

    @BeforeEach
    void setUp() throws IOException {
        // Prepare log data for each test
        List<String> logLines = LogFileLoader.loadLogs(TestDataProvider.SAMPLE_FILE);

        // Create temporary files for the reports inside tempDir
        adocReportPath = Files.createFile(tempDir.resolve(BASE_FILE_NAME + ".adoc"));
        mdReportPath = Files.createFile(tempDir.resolve(BASE_FILE_NAME + ".md"));

        // Parse each log line to populate data for observers
        for (String line : logLines) {
            LogParser.parseLog(line);
        }
    }

    @Test
    @DisplayName("Test Adoc Report Content")
    void testAdocReportContent() throws IOException {
        // Generate the .adoc report using LogReportFormatter
        LogReportFormatter.generateAdocReport(adocReportPath.toString(), LogParser.observers());

        // Read the content from the generated .adoc file
        String adocContent = Files.readString(adocReportPath);

        // Verify that the content includes the expected metrics with the correct format
        assertThat(adocContent).contains("TotalRequest totalRequests", "4");
        assertThat(adocContent).contains("CodeStatuses codeStatuses", "404", "3", "206", "1");
    }

    @Test
    @DisplayName("Test Markdown Report Content")
    void testMarkdownReportContent() throws IOException {
        // Generate the .md report using LogReportFormatter
        LogReportFormatter.generateMarkdownReport(mdReportPath.toString(), LogParser.observers());

        // Read the content from the generated .md file
        String mdContent = Files.readString(mdReportPath);

        // Verify that the content includes the expected metrics with the correct format
        assertThat(mdContent).contains("TotalRequest totalRequests", "4");
        assertThat(mdContent).contains("CodeStatuses codeStatuses", "404", "3", "206", "1");
    }
}
