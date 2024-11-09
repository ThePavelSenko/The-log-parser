package backend.academy.logParseComponents;

import backend.academy.exceptions.LogParseException;
import backend.academy.logObservers.LogObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;


/**
 * Utility class for parsing NGNIX log entries and notifying registered observers of parsed log data.
 *
 * <p>This parser supports both IPv4 and IPv6 formats in log entries and follows the NGNIX log entry pattern.</p>
 */
@Log4j2
@UtilityClass
public final class LogParser {

    /**
     * Regex pattern for parsing NGNIX log entries. The pattern supports both IPv4 and IPv6 addresses.
     * <p>
     * Format of log entry: <code>[IP_ADDRESS] - - [TIMESTAMP] "REQUEST" STATUS_CODE SIZE "REFERRER" "USER_AGENT"</code>
     * </p>
     */
    public static final String LOG_PATTERN =
        "([\\dA-Fa-f:.]+)\\s+-\\s+-\\s+\\[(.*?)]\\s+\"(.*?)\"\\s+(\\d{3})\\s+(\\d+)\\s+\"(.*?)\"\\s+\"(.*?)\"";

    /**
     * List of observers to notify on each log entry parsing.
     * Observers must implement the {@link LogObserver} interface.
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
     * Registers an observer to be notified of each parsed log entry.
     * <p>
     * <b>Note:</b> Improper use of observers may lead to unintended side effects.
     * </p>
     *
     * @param observer the {@link LogObserver} to add
     */
    public static void addObserver(LogObserver observer) {
        OBSERVERS.add(observer);
        log.info("Added observer: {}", observer.getClass().getSimpleName());
    }

    /**
     * Parses a single log line according to the defined {@code LOG_PATTERN} and notifies observers
     * with the parsed log data.
     *
     * @param logLine the log line to parse; expected format:
     *                <code>[IP_ADDRESS] - - [TIMESTAMP] "REQUEST" STATUS_CODE SIZE "REFERRER" "USER_AGENT"</code>
     *                <ul>
     *                  <li>IP_ADDRESS: A valid IPv4 or IPv6 address</li>
     *                  <li>TIMESTAMP: Date in the format <code>dd/MMM/yyyy HH:mm:ss</code></li>
     *                  <li>REQUEST: The HTTP request made</li>
     *                  <li>STATUS_CODE: HTTP status code (e.g., 200, 404)</li>
     *                  <li>SIZE: The size of the response in bytes</li>
     *                  <li>REFERRER: The referring URL (if any)</li>
     *                  <li>USER_AGENT: The client's user agent string</li>
     *                </ul>
     * @return a {@link LogReport} object containing parsed log data
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
     * @param logLine the log line to parse; expected to match {@code LOG_PATTERN}
     * @param pattern the compiled regex pattern for log parsing
     * @return a {@link LogReport} containing parsed log data
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
     * Notifies all registered observers with the provided log entry.
     *
     * @param logReport the parsed log data to send to observers
     */
    private static void notifyObservers(LogReport logReport) {
        for (LogObserver observer : OBSERVERS) {
            try {
                observer.update(logReport);
            } catch (Exception e) {
                log.error("Error notifying observer {}: {}",
                    observer.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    /**
     * Returns a copy of the registered observers list.
     *
     * @return a new list of {@link LogObserver} instances currently registered with this parser
     */
    public List<LogObserver> observers() {
        return new ArrayList<>(OBSERVERS);
    }
}

