package backend.academy;

import backend.academy.config.CliParams;
import com.beust.jcommander.JCommander;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class Main {

    public static void main(String[] args) {
        CliParams params = new CliParams();
        JCommander commander = JCommander.newBuilder()
            .addObject(params)
            .build();

        try {
            // Parse command-line arguments
            commander.parse(args);
            // Run the logic
            params.run();
        } catch (Exception e) {
            log.error("Error parsing command-line arguments: {}", e.getMessage());
            commander.usage();
        }
    }
}
