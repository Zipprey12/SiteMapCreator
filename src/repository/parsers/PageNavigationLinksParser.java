package repository.parsers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import services.links.LinksFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PageNavigationLinksParser implements SiteParser {

    private final LinksFactory linksFactory;

    public PageNavigationLinksParser(LinksFactory factory) {
        linksFactory = factory;
    }

    @Override
    public List<String> parse(String pageAddress) throws IOException {
        Connection connection = Jsoup.connect(pageAddress);
        Document document = connection.get();
        List<String> list = new LinkedList<>();

        var tags = document.select("a[href]");
        for (var tag : tags) {
            String url = tag.attr("href");
            if (url.contains("sendel_ru_dump-postgres-liquibase.sql")) {
                System.out.println("");
            }
            var link = linksFactory.createLink(url);
            if (link == null) {
                continue;
            }
            var absolutePath = linksFactory.getAbsoluteUrl(link);
            if (absolutePath != null) {
                list.add(absolutePath);
            }
        }
        return list;
    }
}
