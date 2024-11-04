package backend.academy.config;

import backend.academy.logParseComponents.Logic;
import com.beust.jcommander.Parameter;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CliParams {
    @Parameter(names = {"--path"}, description = "Path to the file or URL to collect statistics from", required = true)
    private String fileOrUrl;

    @Parameter(names = {"--from"}, description = "Start date in the format dd/MMM/yyyy HH:mm:ss. Optional.")
    private String fromDate;

    @Parameter(names = {"--to"}, description = "End date in the format dd/MMM/yyyy HH:mm:ss. Optional.")
    private String toDate;

    @Parameter(names = {"--filter-field"}, description = "Field to filter the logs. Optional.")
    private String filterField;

    @Parameter(names = {"--filter-value"}, description = "Value to filter the logs. Optional.")
    private String filterValue;

    @Parameter(names = {"--format"}, description = "Output format (e.g., markdown, adoc). Optional.")
    private String format = "adoc"; // Default format

    public void run() {
        // Parse dates using Logic.parseDateTime
        Optional<LocalDateTime> startDate = Logic.parseDateTime(fromDate);
        Optional<LocalDateTime> endDate = Logic.parseDateTime(toDate);

        // Setup observers
        LoggerConfig.setupObservers();

        // Ensure lack or dependence of register
        if (filterField != null) {
            filterField = filterField.toLowerCase();
        }
        if (filterValue != null) {
            filterValue = filterValue.toLowerCase();
        }

        // Call Logic.startLogic with parsed parameters
        Logic.startLogic(fileOrUrl, startDate, endDate,
            filterField, filterValue, format.toLowerCase());

        log.info("Log parsing and report generation completed.");
    }
}
