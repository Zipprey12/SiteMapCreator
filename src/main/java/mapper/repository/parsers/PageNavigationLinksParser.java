package mapper.repository.parsers;

import lombok.extern.slf4j.Slf4j;
import mapper.model.Link;
import mapper.model.ParsingType;
import mapper.services.links.FastLinksFactory;
import mapper.services.links.ILinksFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
public class PageNavigationLinksParser implements LinksParser {

    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(
            List.of("jpg", "jpeg", "png", "gif", "bmp", "svg", "webp")
    );

    private final transient ILinksFactory baseFactory;
    private transient ILinksFactory linksFactory;

    public PageNavigationLinksParser(ILinksFactory factory) {
        this.baseFactory = factory;
        this.linksFactory = factory;
    }

    @Override
    public void setParsingType(ParsingType type) {
        this.linksFactory = switch (type) {
            case FAST -> new FastLinksFactory(baseFactory);
            case EXTENDED -> baseFactory;
        };
    }

    @Override
    public List<Link> parse(String pageAddress) throws IOException {
        Connection connection = Jsoup.connect(pageAddress);
        Document document = connection.get();
        List<Link> list = new LinkedList<>();

        var tags = document.select("a[href]");
        for (var tag : tags) {
            String url = tag.attr("href");
            if (isImage(url)) {
                continue;
            }

            var link = linksFactory.createLink(url);
            if (link != null) {
                list.add(link);
            }
        }
        return list;
    }

    private boolean isImage(String url) {
        var extension = getFileExtension(url);
        if (extension == null) {
            return false;
        }
        return IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    }

    private String getFileExtension(String url) {
        int lastDot = url.lastIndexOf('.');
        if (lastDot == -1) return null;
        return url.substring(lastDot + 1);
    }
}