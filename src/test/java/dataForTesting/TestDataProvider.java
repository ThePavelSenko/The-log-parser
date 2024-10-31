package dataForTesting;

public class TestDataProvider {
    public static final String SAMPLE_FILE =
        "src/test/java/DataForTesting/TestLogFile";

    public static final String SAMPLE_URL =
        "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";

    public static final String SAMPLE_EARLY_LOG = "91.239.186.133 - - " +
        "[17/May/2015:14:05:39 +0000] " +
        "\"GET /downloads/product_2 HTTP/1.1\" " +
        "304 " +
        "1234 " +
        "\"-\" " +
        "\"Debian APT-HTTP/1.3 (0.9.7.9)\"";

    public static final String SAMPLE_LATE_LOG = "91.239.186.133 - - " +
        "[17/May/2015:15:05:01 +0000] " +
        "\"GET /downloads/product_2 HTTP/1.1\" " +
        "304 " +
        "777 " +
        "\"-\" " +
        "\"Debian APT-HTTP/1.3 (0.9.7.9)\"";
}
