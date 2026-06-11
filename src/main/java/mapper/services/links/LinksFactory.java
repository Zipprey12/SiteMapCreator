package mapper.services.links;

import lombok.AccessLevel;
import lombok.Getter;
import mapper.model.Link;
import mapper.model.SiteProtocol;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public abstract class LinksFactory implements Serializable {

    private static final String PROTOCOL_REGEX = "^https?://";
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile(PROTOCOL_REGEX);

    private String domain;
    private String initialPage;
    private String mainPage;

    @Getter(AccessLevel.PRIVATE)
    private String initialPart;

    private SiteProtocol protocol;

    private List<String> initialLinkParts;

    public abstract Link createLink(String path);

    public abstract String getAbsoluteUrl(Link link);

    public List<String> getInitialLinkParts() {
        if (initialLinkParts == null) {
            return List.of();
        }
        return Collections.unmodifiableList(initialLinkParts);
    }

    public void setProtocol(SiteProtocol protocol) {
        this.protocol = protocol;
        initParts(protocol.getValue());
    }
    public boolean trySetInitialParsingPage(String url) {
        this.protocol = null;
        this.domain = null;

        var normalizedUrl = normalizeUrl(url);
        return tryParseUrl(normalizedUrl);
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
            return parseParts(url, parsedProtocol.length());
        }
        return parseParts(url, 0);
    }

    private String parseProtocol(String url) {
        Matcher matcher = PROTOCOL_PATTERN.matcher(url);
        if (matcher.find() && matcher.start() == 0) {
            return matcher.group(0);
        }
        return null;
    }

    private boolean parseParts(String url, int startIndex) {
        while (startIndex < url.length() && url.charAt(startIndex) == '/') {
            startIndex++;
        }
        if (startIndex == url.length()) {
            return false;
        }
        url = url.substring(startIndex);
        initialLinkParts = new LinkedList<>();

        var link = new Link(url);
        initialLinkParts = link.getParts();
        domain = initialLinkParts.getFirst();
        initialPart = url;

        if (protocol != null) {
            initParts(protocol.getValue());
        }
        return true;
    }

    private void initParts(String protocolValue) {
        mainPage = protocolValue + domain;
        initialPage = protocolValue + initialPart;
    }
}
