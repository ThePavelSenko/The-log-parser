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
     * <p>Filters log entries by checking if the specified field in each entry contains the provided value.
     * If field and value are null or blank, it returns the full list of logs unfiltered.
     * If filtering is successful, it then sorts the filtered entries alphabetically.</p>
     *
     * @param logs  the list of log entries to filter, where each entry should follow the pattern defined
     *              in {@link LogParser#LOG_PATTERN}.
     *              Expected format includes an IP address, timestamp, request details, status code, response size,
     *              referrer, and user agent.
     *              Must not be null. If empty, an empty list will be returned.
     *
     * @param field the name of the field to filter by. Accepted values are:
     *              <ul>
     *                  <li>"ip" - for filtering by IP address</li>
     *                  <li>"request" - for filtering by request details</li>
     *                  <li>"code" - for filtering by HTTP status code</li>
     *                  <li>"response_size" - for filtering by response size in bytes</li>
     *                  <li>"referrer" - for filtering by referrer URL</li>
     *                  <li>"agent" - for filtering by user agent</li>
     *              </ul>
     *              If null or blank, filtering will be skipped.
     *
     * @param value the substring to search for within the specified field.
     *              Must be non-null and non-blank to apply filtering.
     *              If null or blank, filtering will be skipped.
     *
     * @return a sorted list of log entries that contain the specified value in the chosen field.
     *         If no valid logs or fields are found, an empty list will be returned.
     *
     * @throws IllegalArgumentException if an invalid field name is provided.
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
     *
     * @param matcher the matcher for the log entry pattern
     * @param field   the log field to extract (e.g., IP address, status code)
     * @return the value of the specified field in the log entry, or null if the field is not found
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

        /**
         * Retrieves the LogField enum based on a field name.
         *
         * @param field the field name as a string
         * @return an Optional containing the corresponding LogField enum, or empty if no match is found
         */
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
