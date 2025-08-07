package repository.parsers;

import model.Link;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import services.links.LinksFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PageNavigationLinksParser implements LinksParser {
    private final LinksFactory linksFactory;

    public PageNavigationLinksParser(LinksFactory factory) {
        linksFactory = factory;
    }

    @Override
    public List<Link> parse(String pageAddress) throws IOException {
        Connection connection = Jsoup.connect(pageAddress);
        Document document = connection.get();
        List<Link> list = new LinkedList<>();

        var tags = document.select("a[href]");
        for (var tag : tags) {
            String url = tag.attr("href");
            var link = linksFactory.createLink(url);
            if (link != null) {
                list.add(link);
            }
        }
        return list;
    }
}
