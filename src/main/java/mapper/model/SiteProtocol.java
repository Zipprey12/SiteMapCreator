package mapper.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum SiteProtocol {
    HTTP("http://"),
    HTTPS("https://");

    private static final Map<String, SiteProtocol> MAP;

    static {
        MAP = Arrays.stream(values())
                .collect(Collectors.toMap(SiteProtocol::getValue, p -> p));
    }

    private final String value;

    SiteProtocol(String value) {
        this.value = value;
    }

    public static SiteProtocol fromValue(String value) {
        return MAP.get(value);
    }

    public String getValue() {
        return value;
    }
}
