package backend.academy;

import backend.academy.logObservers.AverageResponseSizeObserver;
import backend.academy.logObservers.CodeStatusesObserver;
import backend.academy.logObservers.RecourseRequestsObserver;
import backend.academy.logObservers.TotalRequestObserver;
import backend.academy.logParseComponents.LogParser;
import backend.academy.logParseComponents.Logic;
import java.io.PrintStream;
import java.util.Scanner;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) {
        LogParser.addObserver(new TotalRequestObserver());
        LogParser.addObserver(new CodeStatusesObserver());
        LogParser.addObserver(new RecourseRequestsObserver());
        LogParser.addObserver(new AverageResponseSizeObserver());

        try (Scanner scanner = new Scanner(System.in)) {
            PrintStream out = System.out;

            out.println("Input path to file/url in which you want to collect statistics: ");
            String fileOrUrl = scanner.nextLine();

            Logic.startLogic(fileOrUrl);
        }
    }
}
