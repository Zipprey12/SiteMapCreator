package services.links;

import model.Link;
import model.SiteProtocol;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LinksFactory implements Serializable {
    private static final String PROTOCOL_REGEX = "^https?://";
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile(PROTOCOL_REGEX);

    private String domain;
    private SiteProtocol protocol;
    private String initialPage;

    public abstract Link createLink(String path);
    public abstract String getAbsoluteUrl(Link link);

    public SiteProtocol getProtocol() {
        return protocol;
    }

    public String getDomain() {
        return domain;
    }

    public String getInitialPage() {
        if(protocol == null){
            throw new IllegalStateException("Protocol value must be set to create initial page");
        }
        return initialPage;
    }

    public void setProtocol(SiteProtocol protocol) {
        if(protocol != null){
            this.protocol = protocol;
            updateInitialPage();
        }
    }

    public boolean trySetInitialParsingPage(String url) {
        var normalizedUrl = normalizeUrl(url);
        if (tryParseUrl(normalizedUrl)) {
            if (protocol != null) {
                updateInitialPage();
            }
            return true;
        }
        return false;
    }

    protected String normalizeUrl(String url) {
        return url.trim().toLowerCase();
    }

    private boolean tryParseUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String parsedProtocol = parseProtocol(url);
        if (parsedProtocol != null) {
            protocol = SiteProtocol.fromValue(parsedProtocol);
            return parseDomain(url, parsedProtocol.length());
        }
        return parseDomain(url, 0);
    }

    private String parseProtocol(String url) {
        Matcher matcher = PROTOCOL_PATTERN.matcher(url);
        if (matcher.find() && matcher.start() == 0) {
            return matcher.group(0);
        }
        return null;
    }

    private boolean parseDomain(String url, int startIndex) {
        while (startIndex < url.length() && url.charAt(startIndex) == '/') {
            startIndex++;
        }
        if (startIndex == url.length()) {
            return false;
        }

        var splitterIndex = url.indexOf('/', startIndex);
        if (splitterIndex > 0) {
            domain = url.substring(startIndex, splitterIndex);
        } else {
            domain = url.substring(startIndex);
        }
        return true;
    }

    private void updateInitialPage() {
        initialPage = protocol.getValue() + domain;
    }
}
