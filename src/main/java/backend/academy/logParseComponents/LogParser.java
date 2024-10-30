package backend.academy.logParseComponents;

import backend.academy.exceptions.LogParseException;
import backend.academy.logObservers.LogObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public final class LogParser {

    /**
     * Pattern for parsing NGNIX log entries (supports both IPv4 and IPv6).
     */
    public static final String LOG_PATTERN =
        "([\\dA-Fa-f:.]+)\\s+-\\s+-\\s+\\[(.*?)]\\s+\"(.*?)\"\\s+(\\d{3})\\s+(\\d+)\\s+\"(.*?)\"\\s+\"(.*?)\"";

    /**
     * List of observers to notify on each log entry parsing.
     */
    private static final List<LogObserver> OBSERVERS = new ArrayList<>();

    // Group indices in the regex pattern for extracting log fields
    private static final int IP_ADDRESS_GROUP = 1;
    private static final int TIMESTAMP_GROUP = 2;
    private static final int REQUEST_GROUP = 3;
    private static final int STATUS_CODE_GROUP = 4;
    private static final int SIZE_GROUP = 5;
    private static final int REFERRER_GROUP = 6;
    private static final int USER_AGENT_GROUP = 7;

    /**
     * Adds an observer to the list, allowing dynamic registration of observers.
     *
     * @param observer the observer to add
     */
    public static void addObserver(LogObserver observer) {
        OBSERVERS.add(observer);
        log.info("Added observer: {}", observer.getClass().getSimpleName());
    }

    /**
     * Parses a single log line and notifies observers with the parsed log data.
     *
     * @param logLine the log line to parse
     * @return a LogReport object containing parsed log data
     * @throws LogParseException if the log line is null, empty, or does not match the expected format
     */
    public static LogReport parseLog(String logLine) {
        if (logLine == null || logLine.isEmpty()) {
            throw new LogParseException("Log line is empty or null.");
        }

        try {
            Pattern pattern = Pattern.compile(LOG_PATTERN);
            LogReport logReport = getLogReport(logLine, pattern);

            // Notify all registered observers with the parsed log entry
            notifyObservers(logReport);

            return logReport;
        } catch (LogParseException e) {
            log.error("Failed to parse log line due to invalid format: {}", logLine, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while parsing log line: {}", logLine, e);
            throw new LogParseException("Unexpected error while parsing log line: " + logLine, e);
        }
    }

    /**
     * Extracts log report data from a log line using the regex pattern.
     *
     * @param logLine the log line to parse
     * @param pattern the compiled regex pattern for log parsing
     * @return a LogReport containing parsed log data
     * @throws LogParseException if the log line format is invalid
     */
    private static LogReport getLogReport(String logLine, Pattern pattern) {
        Matcher matcher = pattern.matcher(logLine);

        if (!matcher.matches()) {
            throw new LogParseException("Invalid log format: " + logLine);
        }

        return new LogReport(
            matcher.group(IP_ADDRESS_GROUP),
            matcher.group(TIMESTAMP_GROUP),
            matcher.group(REQUEST_GROUP),
            matcher.group(STATUS_CODE_GROUP),
            matcher.group(SIZE_GROUP),
            matcher.group(REFERRER_GROUP),
            matcher.group(USER_AGENT_GROUP)
        );
    }

    /**
     * Notifies all observers with the provided log entry.
     *
     * @param logReport the parsed log data to send to observers
     */
    private static void notifyObservers(LogReport logReport) {
        for (LogObserver observer : OBSERVERS) {
            try {
                observer.update(logReport);
            } catch (Exception e) {
                log.error("Error notifying observer {}: {}", observer.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }
}
