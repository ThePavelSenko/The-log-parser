package backend.academy.config;

import backend.academy.logObservers.AverageResponseSizeObserver;
import backend.academy.logObservers.CodeStatusesObserver;
import backend.academy.logObservers.RecourseRequestsObserver;
import backend.academy.logObservers.RequestsObservers;
import backend.academy.logObservers.ResponseSizePercentileObserver;
import backend.academy.logObservers.TotalRequestObserver;
import backend.academy.logObservers.UniqueUsersObserver;
import backend.academy.logParseComponents.LogParser;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggerConfig {

    public static void setupObservers() {
        LogParser.addObserver(new TotalRequestObserver());
        LogParser.addObserver(new CodeStatusesObserver());
        LogParser.addObserver(new RecourseRequestsObserver());
        LogParser.addObserver(new AverageResponseSizeObserver());
        LogParser.addObserver(new ResponseSizePercentileObserver());
        LogParser.addObserver(new RequestsObservers());
        LogParser.addObserver(new UniqueUsersObserver());
    }
}
