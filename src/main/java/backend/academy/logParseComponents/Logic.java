package backend.academy.logParseComponents;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public final class Logic {

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm:ss", Locale.ENGLISH);
    private static final String MARKDOWN = "markdown";

    public static void startLogic(String fileOrUrl, Optional<LocalDateTime> start, Optional<LocalDateTime> end,
        String field, String value, String format) {
        String fileName = extractFileName(fileOrUrl);
        PrintStream out = System.out;

        try {
            List<String> logsBeforeParse = LogFileLoader.loadLogs(fileOrUrl, start.orElse(null), end.orElse(null));

            // Filter logs by specified field and value
            List<String> filteredLogs = LogFilter.sortLogsByInputFields(logsBeforeParse, field, value);

            // Parse logs and notify observers
            filteredLogs.forEach(LogParser::parseLog);

            // Generate and save report if observers are available
            if (!LogParser.observers().isEmpty()) {
                String reportFileName = fileName + "_log_report." + (format.equals(MARKDOWN) ? "md" : "adoc");

                // Generate report using static methods from LogReportFormatter
                if (MARKDOWN.equals(format)) {
                    LogReportFormatter.generateMarkdownReport(reportFileName, LogParser.observers());
                } else {
                    LogReportFormatter.generateAdocReport(reportFileName, LogParser.observers());
                }

                out.println("Report has been successfully generated and saved to " + reportFileName);
            } else {
                log.warn("No observers available to generate a report.");
            }

        } catch (IOException e) {
            log.error("An error occurred while writing the report file: {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.error("An error occurred while processing logs: {}", e.getMessage());
        }
    }

    private static String extractFileName(String fileOrUrl) {
        String[] folders = fileOrUrl.split("/");
        return folders[folders.length - 1];
    }

    public static Optional<LocalDateTime> parseDateTime(String input) {
        if (input == null || input.isEmpty()) {
            return Optional.empty(); // Allows skipping input
        }
        try {
            return Optional.of(LocalDateTime.parse(input, DATE_FORMATTER));
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
