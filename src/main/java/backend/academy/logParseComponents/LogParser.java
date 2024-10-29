package backend.academy;

import backend.academy.logObservers.*;
import lombok.extern.log4j.Log4j2;
import java.util.*;
import java.util.regex.*;

@Log4j2
public final class LogParser {
    // Pattern for NGNIX log (supports both IPv4 and IPv6)
    public static final String LOG_PATTERN =
        "([\\dA-Fa-f:.]+)\\s+-\\s+-\\s+\\[(.*?)]\\s+\"(.*?)\"\\s+(\\d{3})\\s+(\\d+)\\s+\"(.*?)\"\\s+\"(.*?)\"";
    public static final List<LogObserver> observers = new ArrayList<>();

    // Method to add an observer dynamically at any time
    public static void addObserver(LogObserver observer) {
        observers.add(observer);
        log.info("Added observer: {}", observer.getClass().getSimpleName());
    }

    // Method to parse a log line and notify observers
    public static LogReport parseLog(String logLine) {
        if (logLine == null || logLine.isEmpty()) {
            throw new LogParseException("Log line is empty or null.");
        }

        Pattern pattern = Pattern.compile(LOG_PATTERN);
        Matcher matcher = pattern.matcher(logLine);

        if (!matcher.matches()) {
            throw new LogParseException("Invalid log format: " + logLine);
        }

        LogReport logReport = new LogReport(
            matcher.group(1),
            matcher.group(2),
            matcher.group(3),
            matcher.group(4),
            matcher.group(5),
            matcher.group(6),
            matcher.group(7)
        );

        // Notify each observer with the new log entry
        notifyObservers(logReport);

        return logReport;
    }

    // Method to notify all observers
    private static void notifyObservers(LogReport log) {
        for (LogObserver observer : observers) {
            observer.update(log);
        }
    }
}
