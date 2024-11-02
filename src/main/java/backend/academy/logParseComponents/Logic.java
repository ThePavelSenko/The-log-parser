package backend.academy.logParseComponents;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public final class Logic {

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm:ss", Locale.ENGLISH);
    private static final String PROMPT_START_TIME = "Start time (or press Enter to skip): ";
    private static final String PROMPT_END_TIME = "End time (or press Enter to skip): ";
    private static final String PROMPT_DATE_FORMAT =
        "Invalid format. Please enter the date in format dd/MMM/yyyy HH:mm:ss, e.g., 31/Aug/2024 15:30:00.";
    private static final String MESSAGE_INSTRUCTIONS =
        "Enter the start and end times in the format dd/MMM/yyyy HH:mm:ss. If you want to skip, just press Enter.";
    private static final String PROMPT_FIELD_FILTER = "Enter the field to filter by (e.g., 'ip address'): ";
    private static final String PROMPT_VALUE_FILTER = "Enter the value to filter by: ";

    public static void startLogic(String fileOrURL) {
        String fileName = extractFileName(fileOrURL);
        PrintStream out = System.out;

        try (Scanner scanner = new Scanner(System.in)) {
            out.println(MESSAGE_INSTRUCTIONS);

            Optional<LocalDateTime> start = promptForDateTime(scanner, PROMPT_START_TIME);
            Optional<LocalDateTime> end = promptForDateTime(scanner, PROMPT_END_TIME);

            List<String> logsBeforeParse = LogFileLoader.loadLogs(fileOrURL, start.orElse(null), end.orElse(null));

            // Filter logs by specified field and value
            out.print(PROMPT_FIELD_FILTER);
            String field = scanner.nextLine().trim();
            out.print(PROMPT_VALUE_FILTER);
            String value = scanner.nextLine().trim();

            List<String> filteredLogs = LogFilter.sortLogsByInputFields(logsBeforeParse, field, value);

            // Parse logs and notify observers
            for (String log : filteredLogs) {
                LogParser.parseLog(log);
            }

            // Generate report if observers are available
            if (!LogParser.observers().isEmpty()) {
                LogReportFormatter reportFormatter = new LogReportFormatter(fileName, LogParser.observers());
                out.println(reportFormatter.generateAdocReport());
            } else {
                log.warn("No observers available to generate a report.");
            }

        } catch (IOException | IllegalStateException e) {
            log.error("An error occurred while processing logs: {}", e.getMessage());
        }
    }

    private static String extractFileName(String fileOrURL) {
        String[] folders = fileOrURL.split("/");
        return folders[folders.length - 1];
    }

    private static Optional<LocalDateTime> promptForDateTime(Scanner scanner, String prompt) {
        PrintStream out = System.out;
        while (true) {
            out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return Optional.empty(); // Allows skipping input
            }
            try {
                return Optional.of(LocalDateTime.parse(input, DATE_FORMATTER));
            } catch (DateTimeParseException e) {
                out.println(PROMPT_DATE_FORMAT);
            }
        }
    }
}
