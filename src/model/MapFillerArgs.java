package model;

import repository.parsers.SiteParser;
import services.links.LinksFactory;
import services.map.SiteMap;

import java.util.Set;

public record MapFillerArgs(SiteMap map,
                            SiteParser parser,
                            Set<String> visitedLinks,
                            LinksFactory linksFactory,
                            int maxSearchingLevel) {
}
