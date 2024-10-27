package backend.academy;

import lombok.extern.log4j.*;
import java.util.regex.*;

@Log4j2
public final class LogParser {
    // Pattern for NGNIX log
    private static final String LOG_PATTERN =
        "(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+-\\s+-\\s+\\[(.*?)]\\s+\"(.*?)\"\\s+(\\d+)\\s+(\\d+)\\s+\"(.*?)\"\\s+\"(.*?)\"";

    // Custom exception to handle log parsing errors
    public static LogReport parseLog(String logLine) {
        if (logLine == null || logLine.isEmpty()) {
            throw new LogParseException("Log line is empty or null.");
        }

        Pattern pattern = Pattern.compile(LOG_PATTERN);
        Matcher matcher = pattern.matcher(logLine);

        if (!matcher.matches()) {
            throw new LogParseException("Invalid log format: " + logLine);
        }

        return new LogReport(
            matcher.group(1),
            matcher.group(2),
            matcher.group(3),
            matcher.group(4),
            matcher.group(5),
            matcher.group(6),
            matcher.group(7)
        );
    }
}
