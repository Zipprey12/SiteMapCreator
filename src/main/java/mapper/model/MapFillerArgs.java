package mapper.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mapper.repository.parsers.LinksParser;
import mapper.services.links.ILinksFactory;
import mapper.services.map.SiteMap;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@RequiredArgsConstructor
public class MapFillerArgs {

    private final SiteMap map;
    private final LinksParser parser;
    private final Set<String> visitedLinks;
    private final ILinksFactory linksFactory;

    private final AtomicInteger processedArticlesCount = new AtomicInteger(0);
    private final AtomicInteger failedRequestsCount = new AtomicInteger(0);

    @Setter
    private int maxSearchingLevel;

    @Setter
    private int maxArticlesCount;

    @Setter
    private long requestDelayMs;

}