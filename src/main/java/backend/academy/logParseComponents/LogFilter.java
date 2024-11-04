package backend.academy.logParseComponents;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public final class LogFilter {

    /**
     * Filters and sorts log entries based on the specified field and value.
     *
     * @param logs  the list of logs to filter
     * @param field the field name to filter by
     * @param value the value to filter by
     * @return a sorted list of filtered logs
     */
    public static List<String> sortLogsByInputFields(List<String> logs, String field, String value) {
        if (logs == null || logs.isEmpty()) {
            log.warn("No logs provided for filtering.");
            return List.of();
        }

        if (field == null || field.isBlank() || value == null || value.isBlank()) {
            log.info("Field or value is empty, returning logs without filtering.");
            return new ArrayList<>(logs); // Return all logs without filtering
        }

        Optional<LogField> logFieldOpt = LogField.fromString(field);
        if (logFieldOpt.isEmpty()) {
            log.error("Invalid input field: {}", field);
            throw new IllegalArgumentException("Invalid input field: " + field);
        }

        List<String> filteredLogs = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile(LogParser.LOG_PATTERN);
            for (String logEntry : logs) {
                Matcher matcher = pattern.matcher(logEntry);

                if (matcher.find()) {
                    String fieldValue = extractField(matcher, logFieldOpt.get());
                    if (fieldValue != null && fieldValue.toLowerCase().contains(value.toLowerCase())) {
                        filteredLogs.add(logEntry);
                    }
                }
            }
            filteredLogs.sort(Comparator.naturalOrder()); // Sort alphabetically
        } catch (Exception e) {
            log.error("Unexpected error while filtering logs: {}", e.getMessage(), e);
        }

        return filteredLogs;
    }

    /**
     * Extracts the field value based on the given LogField directly from the matcher.
     */
    private static String extractField(Matcher matcher, LogField field) {
        return matcher.group(field.groupIndex());
    }

    @Getter
    private enum LogField {
        IP_ADDRESS(1, "ip"),
        REQUEST(3, "request"),
        STATUS_CODE(4, "code"),
        RESPONSE_SIZE(5, "response_size"),
        REFERRER(6, "referrer"),
        USER_AGENT(7, "agent");

        private final int groupIndex;
        private final String fieldName;

        LogField(int groupIndex, String fieldName) {
            this.groupIndex = groupIndex;
            this.fieldName = fieldName;
        }

        public static Optional<LogField> fromString(String field) {
            for (LogField logField : values()) {
                if (logField.fieldName.equalsIgnoreCase(field)) {
                    return Optional.of(logField);
                }
            }
            return Optional.empty(); // Return empty Optional if no matching field is found
        }
    }
}
