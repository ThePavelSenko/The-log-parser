package backend.academy.logParseComponents;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class LogFilter {

    public static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    private LogFilter() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Filters and sorts logs by a specified time range.
     *
     * @param logs      the list of logs to filter and sort
     * @param startTime the start of the time range
     * @param endTime   the end of the time range
     * @return a list of filtered and sorted logs within the specified time range
     */
    public static List<LogReport> filterAndSortLogsByTimeRange(
        List<LogReport> logs,
        LocalDateTime startTime,
        LocalDateTime endTime) {
        return logs.stream()
            .map(LogFilter::parseLogTimestamp)
            .flatMap(Optional::stream)  // Only proceed with successfully parsed timestamps
            .filter(entry -> isWithinRange(entry.timestamp(), startTime, endTime))
            .sorted(Comparator.comparing(LogReportWithParsedTime::timestamp))
            .map(LogReportWithParsedTime::logReport)
            .collect(Collectors.toList());
    }

    private static Optional<LogReportWithParsedTime> parseLogTimestamp(LogReport log) {
        try {
            LocalDateTime timestamp = LocalDateTime.parse(log.timestamp(), DATE_FORMATTER);
            return Optional.of(new LogReportWithParsedTime(log, timestamp));
        } catch (DateTimeParseException e) {
            System.err.println("Invalid timestamp format for log: " + log.timestamp());
            return Optional.empty();  // Return empty Optional for logs with invalid timestamp format
        }
    }

    private static boolean isWithinRange(LocalDateTime timestamp, LocalDateTime startTime, LocalDateTime endTime) {
        return (timestamp.isAfter(startTime) || timestamp.isEqual(startTime)) && timestamp.isBefore(endTime);
    }

    private record LogReportWithParsedTime(LogReport logReport, LocalDateTime timestamp) {
    }
}
