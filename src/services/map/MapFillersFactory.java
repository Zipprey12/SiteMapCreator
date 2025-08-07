package services.map;

import model.MapFillerArgs;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import repository.parsers.LinksParser;
import services.links.LinksFactory;
import services.map.filler.SiteMapFiller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class MapFillersFactory {
    private static final int PAGE_EXIST_STATUS_CODE = 200;
    private static final int DEFAULT_MAXIMUM_SEARCHING_LEVEL = 3;
    private static final int DEFAULT_MAX_PREPARED_ARTICLES_COUNT = 3_000;

    private final LinksFactory linksFactory;
    private final LinksParser parser;
    private final Set<String> visitedLinks;
    private final ForkJoinPool pool = new ForkJoinPool();

    private SiteMap map;

    private int maxLevel = DEFAULT_MAXIMUM_SEARCHING_LEVEL;

    public MapFillersFactory(LinksParser parser, LinksFactory factory) {
        this.linksFactory = factory;
        visitedLinks = Collections.synchronizedSet(new HashSet<>());
        this.parser = parser;
    }

    public int getMaximumSearchingLevel() {
        return maxLevel;
    }

    public void setMaximumSearchingLevel(int level) {
        maxLevel = level;
    }

    public LinksFactory getLinksFactory() {
        return linksFactory;
    }

    public SiteMap createMap() {
        reset();

        var initialPage = linksFactory.getInitialPage();
        map.initialize(initialPage, linksFactory.getInitialLinkParts());
        try {
            Connection.Response response = Jsoup.connect(initialPage)
                    .followRedirects(false)
                    .execute();
            if(response.statusCode() != PAGE_EXIST_STATUS_CODE){
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        var filler = createFiller(initialPage);
        pool.execute(filler);
        filler.join();

        return map;
    }

    private void reset(){
        map = new SiteMap();
        visitedLinks.clear();

    }

    private SiteMapFiller createFiller(String url) {
        var args = new MapFillerArgs(map, parser, visitedLinks, linksFactory);
        args.setMaxArticlesCount(DEFAULT_MAX_PREPARED_ARTICLES_COUNT);
        args.setMaxSearchingLevel(maxLevel);

        return new SiteMapFiller(args, 0, url);
    }
}
