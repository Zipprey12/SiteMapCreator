package model;

import repository.parsers.LinksParser;
import services.links.LinksFactory;
import services.map.SiteMap;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MapFillerArgs {
    private final SiteMap map;
    private final LinksParser parser;
    private final LinksFactory linksFactory;
    private final AtomicInteger processedArticlesCount = new AtomicInteger(0);

    private final Set<String> visitedLinks;

    private int maxSearchingLevel;
    private int maxArticlesCount;

    public MapFillerArgs(SiteMap map, LinksParser parser, Set<String> visitedLinks, LinksFactory factory) {
        this.map = map;
        this.parser = parser;
        this.visitedLinks = visitedLinks;
        this.linksFactory = factory;
    }

    public void setMaxSearchingLevel(int maxSearchingLevel) {
        this.maxSearchingLevel = maxSearchingLevel;
    }

    public void setMaxArticlesCount(int maxArticlesCount) {
        this.maxArticlesCount = maxArticlesCount;
    }

    public SiteMap getMap() {
        return map;
    }

    public LinksParser getParser() {
        return parser;
    }

    public LinksFactory getLinksFactory() {
        return linksFactory;
    }

    public int getMaxSearchingLevel() {
        return maxSearchingLevel;
    }

    public int getMaxArticlesCount() {
        return maxArticlesCount;
    }

    public AtomicInteger getProcessedArticlesCount() {
        return processedArticlesCount;
    }

    public Set<String> getVisitedLinks() {
        return visitedLinks;
    }
}