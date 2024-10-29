package backend.academy.logParseComponents;

import backend.academy.exceptions.LogParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public final class LogFileLoader {

    /**
     * Loads and parses log data from a file or URL, then filters the log entries by timestamp if specified.
     *
     * @param fileOrUrl the file path or URL to load logs from
     * @param startTime the start timestamp for filtering logs (inclusive)
     * @param endTime the end timestamp for filtering logs (exclusive)
     * @return a list of log lines matching the criteria
     * @throws IOException if an I/O error occurs during file or URL reading
     */
    public static List<String> loadLogs(String fileOrUrl, LocalDateTime startTime, LocalDateTime endTime)
        throws IOException {
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
            logLines = Files.readAllLines(Path.of(fileOrUrl));
        }

        // Filter log lines by timestamps if startTime and endTime are specified
        return processLogLines(logLines, startTime, endTime);
    }

    /**
     * Overloaded method to load logs without timestamp filtering.
     *
     * @param fileOrUrl the file path or URL to load logs from
     * @return a list of log lines
     * @throws IOException if an I/O error occurs
     */
    public static List<String> loadLogs(String fileOrUrl) throws IOException {
        return loadLogs(fileOrUrl, null, null);
    }

    /**
     * Processes log lines, filtering by timestamp if startTime and endTime are provided.
     *
     * @param logLines the list of log lines to process
     * @param startTime the start timestamp for filtering (inclusive)
     * @param endTime the end timestamp for filtering (exclusive)
     * @return a list of log lines within the specified time range
     */
    private static List<String> processLogLines(List<String> logLines, LocalDateTime startTime, LocalDateTime endTime) {
        List<String> filteredLines = new ArrayList<>();

        for (String line : logLines) {
            try {
                Matcher matcher = Pattern.compile(LogParser.LOG_PATTERN).matcher(line);
                if (matcher.matches()) {
                    String timeStamp = matcher.group(2); // Timestamp is the second group
                    LocalDateTime logTime = parseLogTimestamp(timeStamp);

                    // Add lines within the specified time range, or all lines if range is null
                    if (startTime != null && endTime != null) {
                        if ((logTime.isAfter(startTime) || logTime.isEqual(startTime)) && logTime.isBefore(endTime)) {
                            filteredLines.add(line);
                        }
                    } else {
                        filteredLines.add(line);
                    }
                } else {
                    log.warn("Log line does not match the pattern: {}", line);
                }
            } catch (LogParseException e) {
                log.error("Failed to parse log line: {}", line, e);
            }
        }
        return filteredLines;
    }

    /**
     * Parses the log timestamp into a LocalDateTime.
     *
     * @param timestamp the timestamp string from a log entry
     * @return the parsed LocalDateTime
     */
    private static LocalDateTime parseLogTimestamp(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        return LocalDateTime.parse(timestamp, formatter);
    }

    /**
     * Determines if the provided path is a URL.
     *
     * @param path the path to check
     * @return true if the path is a URL, false otherwise
     */
    private static boolean isUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }
}
