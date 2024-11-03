package backend.academy.logParseComponents;

import backend.academy.logObservers.LogObserver;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

/**
 * Utility class for generating log reports in different formats.
 */
@Log4j2
@UtilityClass
public class LogReportFormatter {

    // Constants to avoid magic numbers and duplicate string literals
    private static final String METRIC_HEADER = "Metric";
    private static final String VALUE_HEADER = "Value";
    private static final String KEY_HEADER = "Key";
    private static final String PIPE_SPACE = " | ";
    private static final String PIPE_NEWLINE = " |\n";
    private static final String MARKDOWN_PIPE_HEADER = "| Metric | Value |\n|--------|-------|\n";
    private static final String ADOC_MAP_HEADER_TEMPLATE = "#### %s (Map)\n\n";
    private static final String MARKDOWN_MAP_HEADER_TEMPLATE = "\n## %s (Map)\n\n";
    private static final String BACKTICK_LOG_FILE = "`";
    private static final String NEWLINE = "\n";
    private static final String DOUBLE_NEWLINE = "\n\n";
    private static final int DEFAULT_METRIC_WIDTH = 6;
    private static final int DEFAULT_VALUE_WIDTH = 5;
    private static final int DEFAULT_KEY_WIDTH = 3;

    /**
     * Generates an AsciiDoc report from log observers' metrics.
     *
     * @param fileName  the name of the file to write the report to
     * @param observers  the list of log observers
     */
    public static void generateAdocReport(String fileName, List<LogObserver> observers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("## Log Report" + DOUBLE_NEWLINE);
            writer.write("#### General Information" + NEWLINE);
            writer.write("Log File(s): "
                + BACKTICK_LOG_FILE + fileName + BACKTICK_LOG_FILE + NEWLINE + DOUBLE_NEWLINE);

            // Process metrics and write reports
            processAndWriteMetrics(writer, observers, true);

            log.info("Report successfully written to file: {}", fileName);
        } catch (IOException e) {
            log.error("Failed to write report to file: {}", e.getMessage(), e);
        }
    }

    /**
     * Generates a Markdown report from log observers' metrics.
     *
     * @param fileName  the name of the file to write the report to
     * @param observers  the list of log observers
     */
    public static void generateMarkdownReport(String fileName, List<LogObserver> observers) {
        String markdownFileName = fileName.replace(".adoc", ".md");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(markdownFileName))) {
            writer.write("# Log Report" + NEWLINE + NEWLINE);
            writer.write("## General Information" + NEWLINE);
            writer.write("**Log File(s):** "
                + BACKTICK_LOG_FILE + fileName + BACKTICK_LOG_FILE + NEWLINE + DOUBLE_NEWLINE);

            // Process metrics and write reports
            processAndWriteMetrics(writer, observers, false);

            log.info("Markdown report successfully written to file: {}", markdownFileName);
        } catch (IOException e) {
            log.error("Failed to write markdown report to file: {}", e.getMessage(), e);
        }
    }

    /**
     * Processes metrics from observers and writes to the specified writer in the given format.
     *
     * @param writer     the BufferedWriter to write to
     * @param observers  the list of log observers
     * @param isAdoc    flag indicating if the format is AsciiDoc; false for Markdown
     * @throws IOException if an I/O error occurs
     */
    private static void processAndWriteMetrics(
        BufferedWriter writer,
        List<LogObserver> observers,
        boolean isAdoc) throws IOException {
        List<String[]> singleValueMetrics = new ArrayList<>();
        Map<String, Map<?, ?>> mapMetrics = new LinkedHashMap<>();

        // Collect metrics from each observer
        for (LogObserver observer : observers) {
            String observerName = observer.getClass().getSimpleName().replace("Observer", "");
            processObserverMetrics(observer, observerName, singleValueMetrics, mapMetrics);
        }

        // Format and write single-value metrics
        if (isAdoc) {
            formatSingleValueMetricsAdoc(writer, singleValueMetrics);
        } else {
            formatSingleValueMetricsMarkdown(writer, singleValueMetrics);
        }

        // Format and write map metrics
        if (isAdoc) {
            formatMapMetricsAdoc(writer, mapMetrics);
        } else {
            formatMapMetricsMarkdown(writer, mapMetrics);
        }
    }

    /**
     * Processes metrics for a single observer and adds them to the respective lists.
     *
     * @param observer          the log observer
     * @param observerName      the name of the observer
     * @param singleValueMetrics the list to store single-value metrics
     * @param mapMetrics        the map to store map metrics
     */
    private static void processObserverMetrics(LogObserver observer, String observerName,
        List<String[]> singleValueMetrics,
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
     * Formats and writes single-value metrics in AsciiDoc format.
     *
     * @param writer              the BufferedWriter to write to
     * @param singleValueMetrics  the list of single-value metrics
     * @throws IOException if an I/O error occurs
     */
    private static void formatSingleValueMetricsAdoc(BufferedWriter writer,
        List<String[]> singleValueMetrics) throws IOException {
        int maxMetricWidth = Math.max(METRIC_HEADER.length(),
            singleValueMetrics.stream().mapToInt(arr -> arr[0].length()).max().orElse(DEFAULT_METRIC_WIDTH));
        int maxValueWidth = Math.max(VALUE_HEADER.length(),
            singleValueMetrics.stream().mapToInt(arr -> arr[1].length()).max().orElse(DEFAULT_VALUE_WIDTH));

        writer.write(PIPE_SPACE);
        writer.write(String.format("%-" + maxMetricWidth + "s", METRIC_HEADER));
        writer.write(PIPE_SPACE);
        writer.write(String.format("%-" + maxValueWidth + "s", VALUE_HEADER));
        writer.write(PIPE_NEWLINE);

        for (String[] metric : singleValueMetrics) {
            writer.write(PIPE_SPACE);
            writer.write(String.format("%-" + maxMetricWidth + "s", metric[0]));
            writer.write(PIPE_SPACE);
            writer.write(String.format("%-" + maxValueWidth + "s", metric[1]));
            writer.write(PIPE_NEWLINE);
        }
    }

    /**
     * Formats and writes single-value metrics in Markdown format.
     *
     * @param writer              the BufferedWriter to write to
     * @param singleValueMetrics  the list of single-value metrics
     * @throws IOException if an I/O error occurs
     */
    private static void formatSingleValueMetricsMarkdown(BufferedWriter writer,
        List<String[]> singleValueMetrics) throws IOException {
        writer.write(MARKDOWN_PIPE_HEADER);

        for (String[] metric : singleValueMetrics) {
            writer.write("| " + metric[0] + PIPE_SPACE + metric[1] + PIPE_NEWLINE);
        }
    }

    /**
     * Formats and writes map metrics in AsciiDoc format.
     *
     * @param writer      the BufferedWriter to write to
     * @param mapMetrics  the map of metrics
     * @throws IOException if an I/O error occurs
     */
    private static void formatMapMetricsAdoc(BufferedWriter writer,
        Map<String, Map<?, ?>> mapMetrics) throws IOException {
        for (Map.Entry<String, Map<?, ?>> entry : mapMetrics.entrySet()) {
            String metricName = entry.getKey();
            Map<?, ?> map = entry.getValue();

            writer.write(String.format(ADOC_MAP_HEADER_TEMPLATE, metricName));

            int maxKeyWidth = Math.max(KEY_HEADER.length(),
                map.keySet().stream().mapToInt(key -> key.toString().length()).max().orElse(DEFAULT_KEY_WIDTH));
            int maxMapValueWidth = Math.max(VALUE_HEADER.length(),
                map.values().stream().mapToInt(value -> value.toString().length()).max().orElse(DEFAULT_VALUE_WIDTH));

            writer.write(PIPE_SPACE);
            writer.write(String.format("%-" + maxKeyWidth + "s", KEY_HEADER));
            writer.write(PIPE_SPACE);
            writer.write(String.format("%-" + maxMapValueWidth + "s", VALUE_HEADER));
            writer.write(PIPE_NEWLINE);

            for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
                writer.write(PIPE_SPACE);
                writer.write(String.format("%-" + maxKeyWidth + "s", mapEntry.getKey()));
                writer.write(PIPE_SPACE);
                writer.write(String.format("%-" + maxMapValueWidth + "s", mapEntry.getValue()));
                writer.write(PIPE_NEWLINE);
            }
        }
    }

    /**
     * Formats and writes map metrics in Markdown format.
     *
     * @param writer      the BufferedWriter to write to
     * @param mapMetrics  the map of metrics
     * @throws IOException if an I/O error occurs
     */
    private static void formatMapMetricsMarkdown(BufferedWriter writer,
        Map<String, Map<?, ?>> mapMetrics) throws IOException {
        for (Map.Entry<String, Map<?, ?>> entry : mapMetrics.entrySet()) {
            String metricName = entry.getKey();
            Map<?, ?> map = entry.getValue();

            writer.write(String.format(MARKDOWN_MAP_HEADER_TEMPLATE, metricName));
            writer.write("| Key | Value |\n");
            writer.write("|-----|-------|\n");

            for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
                writer.write("| " + mapEntry.getKey() + PIPE_SPACE + mapEntry.getValue() + PIPE_NEWLINE);
            }
        }
    }

    /**
     * Creates a friendly metric name from the observer name and method name.
     *
     * @param observerName the name of the observer
     * @param methodName   the name of the method
     * @return a formatted friendly name
     */
    private static String createFriendlyName(String observerName, String methodName) {
        return observerName + " " + methodName;
    }
}
