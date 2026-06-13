package mapper;

import mapper.repository.parsers.LinksParser;
import mapper.repository.parsers.PageNavigationLinksParser;
import mapper.services.Navigator;
import mapper.services.commands.*;
import mapper.services.links.ILinksFactory;
import mapper.services.links.RelativeLinksFactory;
import mapper.services.map.filler.MapFillersFactory;

public class Main {
    public static void main(String[] args) {
        ILinksFactory linksFactory = new RelativeLinksFactory();
        LinksParser parser = new PageNavigationLinksParser(linksFactory);
        MapFillersFactory factory = new MapFillersFactory(parser, linksFactory);

        Navigator navigator = new Navigator();
        navigator.add(new SearchingLevelLimitCommand(factory))
                .add(new MaxArticlesCountCommand(factory))
                .add(new RequestDelayCommand(factory))
                .add(new SetParsingTypeCommand(factory))
                .add(new CreateMapCommand(factory))
                .add(new ExitCommand(navigator))
                .start();
    }
}
