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

/**
 * Utility class that contains methods for parsing log files, filtering log entries,
 * and generating reports in different formats (Markdown or AsciiDoc).
 */
@Log4j2
@UtilityClass
public final class Logic {

    /**
     * A constant date formatter for parsing date-time strings from log entries.
     */
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm:ss", Locale.ENGLISH);

    /**
     * The constant value representing the markdown report format.
     */
    private static final String MARKDOWN = "markdown";

    /**
     * Processes the logs by loading them from a file or URL, filtering, parsing,
     * and generating a report based on the provided parameters.
     *
     * @param fileOrUrl The path to the log file or URL to load logs from.
     * @param start Optional start time for filtering logs. If not provided, no start filter is applied.
     * @param end Optional end time for filtering logs. If not provided, no end filter is applied.
     * @param field The field of the log entry to filter by.
     * @param value The value of the field to filter by.
     * @param format The format of the report to generate. Can be "markdown" or "adoc" (AsciiDoc).
     * @throws RuntimeException if an error occurs while processing logs or writing the report.
     */
    public static void startLogic(String fileOrUrl, Optional<LocalDateTime> start, Optional<LocalDateTime> end,
        String field, String value, String format) {
        String fileName = extractFileName(fileOrUrl);
        PrintStream out = System.out;

        try {
            // Load the logs based on time filters
            List<String> logsBeforeParse =
                LogFileLoader.loadLogs(fileOrUrl, start.orElse(null), end.orElse(null));

            // Filter logs by the specified field and value
            List<String> filteredLogs = LogFilter.sortLogsByInputFields(logsBeforeParse, field, value);

            // Parse each log and notify observers
            filteredLogs.forEach(LogParser::parseLog);

            // Generate and save the report if observers are available
            if (!LogParser.observers().isEmpty()) {
                String reportFileName = fileName + "_log_report." + (MARKDOWN.equals(format) ? "md" : "adoc");

                // Generate the report in the specified format
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
            throw new RuntimeException("Error generating report", e);
        } catch (IllegalStateException e) {
            log.error("An error occurred while processing logs: {}", e.getMessage());
            throw new RuntimeException("Error processing logs", e);
        }
    }

    /**
     * Extracts the file name from a given file path or URL.
     *
     * @param fileOrUrl The file path or URL to extract the file name from.
     * @return The extracted file name.
     */
    private static String extractFileName(String fileOrUrl) {
        String[] folders = fileOrUrl.split("/");
        return folders[folders.length - 1];
    }

    /**
     * Parses a date-time string into a LocalDateTime object based on the predefined date format.
     *
     * @param input The date-time string to parse.
     * @return An Optional containing the parsed LocalDateTime if successful,
     * or an empty Optional if the input is invalid or empty.
     */
    public static Optional<LocalDateTime> parseDateTime(String input) {
        if (input == null || input.isEmpty()) {
            return Optional.empty(); // Allows skipping input if it's empty or null
        }
        try {
            return Optional.of(LocalDateTime.parse(input, DATE_FORMATTER));
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: {}", e.getMessage());
            return Optional.empty(); // Return an empty Optional for invalid date format
        }
    }
}
