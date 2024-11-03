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
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LogReportFormatterTest {
    private static final String BASE_FILE_NAME = "TestLogFile";
    private static Path adocReportPath;
    private static Path mdReportPath;

    @BeforeAll
    static void setUp() throws IOException {
        LogParser.addObserver(new TotalRequestObserver());
        LogParser.addObserver(new CodeStatusesObserver());

        List<String> logLines = LogFileLoader.loadLogs(TestDataProvider.SAMPLE_FILE);

        // Define paths for the reports
        adocReportPath = Path.of(BASE_FILE_NAME + ".adoc");
        mdReportPath = Path.of(BASE_FILE_NAME + ".md");

        for (String line : logLines) {
            LogParser.parseLog(line);
        }
    }

    @Test
    void testAdocReportContent() throws IOException {
        LogReportFormatter.generateAdocReport(adocReportPath.toString(), LogParser.observers());
        // Read content from the generated .adoc file
        String adocContent = Files.readString(adocReportPath);

        // Check if content contains expected metrics with updated formatting
        assertThat(adocContent).contains("TotalRequest totalRequests", "4");
        assertThat(adocContent).contains("CodeStatuses codeStatuses", "404", "3", "206", "1");
    }

    @Test
    public void testMarkdownReportContent() throws Exception {
        LogReportFormatter.generateMarkdownReport(mdReportPath.toString(), LogParser.observers());
        String reportFileName = BASE_FILE_NAME + ".md";

        // Check if the file exists
        Path path = Paths.get(reportFileName);
        if (!Files.exists(path)) {
            throw new IllegalStateException("Report file was not created: " + path.toAbsolutePath());
        }

        // Read content from the generated .md file
        String mdContent = Files.readString(path);

        // Check if content contains expected metrics with updated formatting
        assertThat(mdContent).contains("TotalRequest totalRequests", "4");
        assertThat(mdContent).contains("CodeStatuses codeStatuses", "404", "3", "206", "1");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete generated report files after tests
        Files.deleteIfExists(adocReportPath);
        Files.deleteIfExists(mdReportPath);
    }
}
