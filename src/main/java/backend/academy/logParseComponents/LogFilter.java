package backend.academy.logParseComponents;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@UtilityClass
public final class LogFilter {

    private static final List<String> POSSIBLE_FIELDS = List.of(
        "ip address", "request", "status code", "response size", "referrer", "user agent"
    );

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
            return new ArrayList<>(logs); // Возвращаем все логи без фильтрации
        }

        String fieldLower = field.toLowerCase();
        String valueLower = value.toLowerCase();

        if (!POSSIBLE_FIELDS.contains(fieldLower)) {
            log.error("Invalid input field: {}", field);
            throw new IllegalArgumentException("Invalid input field: " + field);
        }

        List<String> filteredLogs = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile(LogParser.LOG_PATTERN);
            for (String logEntry : logs) {
                Matcher matcher = pattern.matcher(logEntry);

                if (matcher.find()) {
                    String fieldValue = extractField(matcher, fieldLower);
                    if (fieldValue != null && fieldValue.toLowerCase().contains(valueLower)) {
                        filteredLogs.add(logEntry);
                    }
                }
            }
            filteredLogs.sort(Comparator.naturalOrder()); // Сортировка по алфавиту
        } catch (Exception e) {
            log.error("Unexpected error while filtering logs: {}", e.getMessage(), e);
        }

        return filteredLogs;
    }

    /**
     * Extracts the field value based on the given field name directly from the matcher.
     */
    private static String extractField(Matcher matcher, String field) {
        return switch (field) {
            case "ip address" -> matcher.group(1);
            case "request" -> matcher.group(3);
            case "status code" -> matcher.group(4);
            case "response size" -> matcher.group(5);
            case "referrer" -> matcher.group(6);
            case "user agent" -> matcher.group(7);
            default -> null;
        };
    }
}
