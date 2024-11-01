package backend.academy.logParseComponents;

import backend.academy.logObservers.LogObserver;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LogReportFormatter {
    private final String fileName;
    private final List<LogObserver> observers;

    // Constants to avoid magic numbers and duplicate string literals
    private static final String METRIC_HEADER = "Metric";
    private static final String VALUE_HEADER = "Value";
    private static final String KEY_HEADER = "Key";
    private static final String PIPE_SPACE = " | ";
    private static final String PIPE_NEWLINE = " |\n";
    private static final int DEFAULT_METRIC_WIDTH = 6;
    private static final int DEFAULT_VALUE_WIDTH = 5;
    private static final int DEFAULT_KEY_WIDTH = 3;

    public LogReportFormatter(String fileName, List<LogObserver> observers) {
        this.fileName = fileName;
        this.observers = observers;
    }

    /**
     * Generates an AsciiDoc-formatted report based on the metrics gathered from observers.
     *
     * @return the formatted report as a String
     */
    public String generateAdocReport() {
        StringBuilder report = new StringBuilder();
        report.append("## Log Report\n\n")
            .append("#### General Information\n")
            .append("Log File(s): `").append(fileName).append("`\n\n");

        List<String[]> singleValueMetrics = new ArrayList<>();
        Map<String, Map<?, ?>> mapMetrics = new LinkedHashMap<>();

        // Collect metrics from each observer
        for (LogObserver observer : observers) {
            String observerName = observer.getClass().getSimpleName().replace("Observer", "");
            processObserverMetrics(observer, observerName, singleValueMetrics, mapMetrics);
        }

        // Format single-value metrics
        formatSingleValueMetrics(report, singleValueMetrics);

        // Format map metrics
        formatMapMetrics(report, mapMetrics);

        return report.toString();
    }

    /**
     * Processes each observer's methods to collect metrics.
     */

    private void processObserverMetrics(LogObserver observer, String observerName, List<String[]> singleValueMetrics,
        Map<String, Map<?, ?>> mapMetrics) {
        for (Method method : observer.getClass().getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0) {
                try {
                    Object value = method.invoke(observer);
                    String metricName = createFriendlyName(observerName, method.getName());

                    if (value instanceof Map) {
                        mapMetrics.put(metricName, (Map<?, ?>) value);
                    } else {
                        singleValueMetrics.add(new String[]{metricName, String.valueOf(value)});
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("Failed to invoke method {} on observer {}: {}",
                        method.getName(), observerName, e.getMessage());
                }
            }
        }
    }

    /**
     * Formats and appends single-value metrics to the report.
     */
    private void formatSingleValueMetrics(StringBuilder report, List<String[]> singleValueMetrics) {
        int maxMetricWidth = Math.max(METRIC_HEADER.length(),
            singleValueMetrics.stream().mapToInt(arr -> arr[0].length()).max().orElse(DEFAULT_METRIC_WIDTH));
        int maxValueWidth = Math.max(VALUE_HEADER.length(),
            singleValueMetrics.stream().mapToInt(arr -> arr[1].length()).max().orElse(DEFAULT_VALUE_WIDTH));

        report.append(PIPE_SPACE)
            .append(String.format("%-" + maxMetricWidth + "s", METRIC_HEADER))
            .append(PIPE_SPACE)
            .append(String.format("%-" + maxValueWidth + "s", VALUE_HEADER))
            .append(PIPE_NEWLINE);

        for (String[] metric : singleValueMetrics) {
            report.append(PIPE_SPACE)
                .append(String.format("%-" + maxMetricWidth + "s", metric[0]))
                .append(PIPE_SPACE)
                .append(String.format("%-" + maxValueWidth + "s", metric[1]))
                .append(PIPE_NEWLINE);
        }
    }

    /**
     * Formats and appends map metrics to the report.
     */
    private void formatMapMetrics(StringBuilder report, Map<String, Map<?, ?>> mapMetrics) {
        for (Map.Entry<String, Map<?, ?>> entry : mapMetrics.entrySet()) {
            String metricName = entry.getKey();
            Map<?, ?> map = entry.getValue();

            report.append("\n#### ").append(metricName).append(" (Map)\n\n");

            int maxKeyWidth = Math.max(KEY_HEADER.length(),
                map.keySet().stream().mapToInt(key -> key.toString().length()).max().orElse(DEFAULT_KEY_WIDTH));
            int maxMapValueWidth = Math.max(VALUE_HEADER.length(),
                map.values().stream().mapToInt(value -> value.toString().length()).max().orElse(DEFAULT_VALUE_WIDTH));

            report.append(PIPE_SPACE)
                .append(String.format("%-" + maxKeyWidth + "s", KEY_HEADER))
                .append(PIPE_SPACE)
                .append(String.format("%-" + maxMapValueWidth + "s", VALUE_HEADER))
                .append(PIPE_NEWLINE);

            for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
                report.append(PIPE_SPACE)
                    .append(String.format("%-" + maxKeyWidth + "s", mapEntry.getKey()))
                    .append(PIPE_SPACE)
                    .append(String.format("%-" + maxMapValueWidth + "s", mapEntry.getValue()))
                    .append(PIPE_NEWLINE);
            }
        }
    }

    /**
     * Converts method names into a user-friendly format by inserting spaces before camel-case transitions.
     *
     * @param observerName the name of the observer
     * @param methodName   the name of the method
     * @return a formatted string representing the metric name
     */
    private String createFriendlyName(String observerName, String methodName) {
        String friendlyName = methodName.replaceAll("([a-z])([A-Z])", "$1 $2");
        return observerName + " - " + friendlyName;
    }
}
