package backend.academy;

import lombok.*;

public record LogReport(String ipAddress,
                        String timestamp,
                        String request,
                        String httpStatusCode,
                        String responseSize,
                        String referrer,
                        String userAgent) {}
