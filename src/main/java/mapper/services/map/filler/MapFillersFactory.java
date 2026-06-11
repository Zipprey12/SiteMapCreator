package mapper.services.map.filler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mapper.model.MapFillerArgs;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import mapper.repository.parsers.LinksParser;
import mapper.services.links.LinksFactory;
import mapper.services.map.SiteMap;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class MapFillersFactory {
    private static final int PAGE_EXIST_STATUS_CODE = 200;
    private static final int DEFAULT_MAXIMUM_SEARCHING_LEVEL = 3;
    private static final int DEFAULT_MAX_PREPARED_ARTICLES_COUNT = 3_000;

    @Getter
    private final LinksFactory linksFactory;

    private final LinksParser parser;
    private final Set<String> visitedLinks;
    private final ForkJoinPool pool = new ForkJoinPool();

    private SiteMap map;
    private int maxLevel = DEFAULT_MAXIMUM_SEARCHING_LEVEL;

    @Getter
    @Setter
    private long requestDelayMs = 150;

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

    public SiteMap createMap() {
        reset();

        log.info("Происходит парсинг данных");

        var initialPage = linksFactory.getInitialPage();
        var mainPart = linksFactory.getMainPage();
        map.initialize(mainPart, linksFactory.getInitialLinkParts());

        try {
            Connection.Response response = Jsoup.connect(initialPage)
                    .followRedirects(true)
                    .execute();
            if (response.statusCode() != PAGE_EXIST_STATUS_CODE) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        var args = createArgs();
        var filler = new SiteMapFiller(args, initialPage, 0);
        pool.execute(filler);
        filler.join();

        log.info("Обход завершён. Обработано страниц: {}. Неудачных запросов: {}",
                args.getProcessedArticlesCount().get(),
                args.getFailedRequestsCount().get());
        return map;
    }

    private void reset() {
        map = new SiteMap();
        visitedLinks.clear();
    }

    private MapFillerArgs createArgs() {
        var args = new MapFillerArgs(map, parser, visitedLinks, linksFactory);
        args.setMaxArticlesCount(DEFAULT_MAX_PREPARED_ARTICLES_COUNT);
        args.setMaxSearchingLevel(maxLevel);
        args.setRequestDelayMs(requestDelayMs);
        return args;
    }
}
