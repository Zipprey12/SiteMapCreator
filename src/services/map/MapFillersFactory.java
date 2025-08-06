package services.map;

import model.MapFillerArgs;
import org.jsoup.Jsoup;
import repository.parsers.SiteParser;
import services.links.LinksFactory;
import services.map.filler.SiteMapFiller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class MapFillersFactory {
    private static final int DEFAULT_MAXIMUM_SEARCHING_LEVEL = 3;

    private final LinksFactory linksFactory;
    private final SiteMap map;
    private final SiteParser parser;
    private final Set<String> visitedLinks;
    private final ForkJoinPool pool = new ForkJoinPool();

    private int maxLevel = DEFAULT_MAXIMUM_SEARCHING_LEVEL;

    public MapFillersFactory(SiteParser parser, LinksFactory factory) {
        this.linksFactory = factory;
        map = new SiteMap();
        visitedLinks = Collections.synchronizedSet(new HashSet<>());
        this.parser = parser;
    }

    public int getMaximumSearchingLevel() {
        return maxLevel;
    }

    public void setMaximumSearchingLevel(int level) {
        maxLevel = level;
    }

    public LinksFactory getLinksFactory(){
        return linksFactory;
    }

    public SiteMap createMap() {
        map.initialize(linksFactory.getInitialPage(), linksFactory.getDomain());

        var initialPage = linksFactory.getInitialPage();
        try {
            Jsoup.connect(initialPage);
        }
        catch (Exception e){
            return null;
        }

        var filler = createFiller(initialPage);
        pool.execute(filler);
        filler.join();

        return map;
    }

    private SiteMapFiller createFiller(String url) {
        var args = new MapFillerArgs(map, parser, visitedLinks, linksFactory, maxLevel);
        return new SiteMapFiller(args, 0, url);
    }
}
