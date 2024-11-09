package backend.academy.logParseComponents;

import backend.academy.exceptions.LogParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

/**
 * Utility class for loading log files or URLs and filtering log entries by timestamp.
 * This class provides methods for:
 * - Loading logs from both file paths and URLs.
 * - Validating the input path or URL for accessibility.
 * - Parsing timestamps from log entries and filtering them by a specified time range.
 *
 * Supported timestamp format for logs: "dd/MMM/yyyy:HH:mm:ss Z" (e.g., "12/Oct/2024:15:32:45 +0000").
 */
@Log4j2
@UtilityClass
public final class LogFileLoader {

    /**
     * Loads log data from a specified file path or URL, with optional filtering by timestamp range.
     *
     * @param fileOrUrl the file path or URL to load logs from. Can be a local file path or a valid HTTP/HTTPS URL.
     * @param startTime the starting timestamp to filter logs (inclusive). If null, no lower bound is applied.
     * @param endTime the ending timestamp to filter logs (exclusive). If null, no upper bound is applied.
     * @return a list of log lines that fall within the specified timestamp range.
     * @throws IOException if an I/O error occurs while reading the file or URL.
     * @throws LogParseException if the input path is invalid or the file is inaccessible.
     */
    public static List<String> loadLogs(String fileOrUrl, LocalDateTime startTime, LocalDateTime endTime)
        throws IOException, LogParseException {

        validateInputPath(fileOrUrl); // Ensure the path or URL is valid
        List<String> logLines = new ArrayList<>();

        if (isUrl(fileOrUrl)) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new URL(fileOrUrl).openStream()))) {
                String line;
                while ((line = buffer.readLine()) != null) {
                    logLines.add(line);
                }
            } catch (IOException e) {
                log.error("Failed to read log data from URL: {}", fileOrUrl, e);
                throw e;
            }
        } else {
            logLines = Files.readAllLines(Paths.get(fileOrUrl));
        }

        return processLogLines(logLines, startTime, endTime); // Filter logs by timestamps
    }

    /**
     * Loads all log data from a specified file path or URL without filtering by timestamp.
     *
     * @param fileOrUrl the file path or URL to load logs from.
     * @return a list of all log lines from the specified source.
     * @throws IOException if an I/O error occurs while reading the file or URL.
     */
    public static List<String> loadLogs(String fileOrUrl) throws IOException {
        return loadLogs(fileOrUrl, null, null);
    }

    /**
     * Validates the specified path or URL for accessibility.
     * - If a URL is provided, it checks if the URL is reachable.
     * - If a file path is provided, it checks if the file exists and is readable.
     *
     * @param path the file path or URL to validate.
     * @throws LogParseException if the path or URL is inaccessible or has an invalid format.
     */
    private static void validateInputPath(String path) throws LogParseException {
        if (isUrl(path)) {
            try {
                URL url = new URL(path);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    log.info("URL is valid and accessible: {}", path);
                } catch (IOException e) {
                    log.error("URL is not accessible: {}", path, e);
                    throw new LogParseException("URL is not accessible: " + path);
                }
            } catch (IOException e) {
                log.error("Invalid URL format: {}", path, e);
                throw new LogParseException("Invalid URL format: " + path);
            }
        } else {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                log.error("The file does not exist or is not readable: {}", path);
                throw new LogParseException("Invalid file path: " + path);
            }
        }
    }

    /**
     * Processes log entries, applying optional filtering by a specified timestamp range.
     *
     * @param logLines the list of log lines to process.
     * @param startTime the starting timestamp to filter logs (inclusive). If null, no lower bound is applied.
     * @param endTime the ending timestamp to filter logs (exclusive). If null, no upper bound is applied.
     * @return a list of log entries that match the specified timestamp range.
     */
    private static List<String> processLogLines(List<String> logLines, LocalDateTime startTime, LocalDateTime endTime) {
        List<String> filteredLines = new ArrayList<>();

        for (String line : logLines) {
            try {
                Matcher matcher = Pattern.compile(LogParser.LOG_PATTERN).matcher(line);
                if (matcher.matches()) {
                    String timeStamp = matcher.group(2); // Extract timestamp from log entry
                    LocalDateTime logTime = parseLogTimestamp(timeStamp);

                    boolean isAfterOrEqualStart = startTime == null || !logTime.isBefore(startTime);
                    boolean isBeforeEnd = endTime == null || logTime.isBefore(endTime);

                    if (isAfterOrEqualStart && isBeforeEnd) {
                        filteredLines.add(line);
                    }
                } else {
                    log.warn("Log line does not match the expected pattern: {}", line);
                }
            } catch (LogParseException e) {
                log.error("Failed to parse log line: {}", line, e);
            }
        }
        return filteredLines;
    }

    /**
     * Converts a timestamp string from a log entry to a LocalDateTime.
     *
     * @param timestamp the timestamp string to parse, expected in the format "dd/MMM/yyyy:HH:mm:ss Z".
     * @return the parsed LocalDateTime.
     */
    private static LocalDateTime parseLogTimestamp(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        return LocalDateTime.parse(timestamp, formatter);
    }

    /**
     * Checks if the provided path is a URL.
     *
     * @param path the path to check.
     * @return true if the path starts with "http://" or "https://", otherwise false.
     */
    private static boolean isUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }
}
