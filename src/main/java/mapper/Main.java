package mapper;

import mapper.repository.parsers.LinksParser;
import mapper.repository.parsers.PageNavigationLinksParser;
import mapper.services.Navigator;
import mapper.services.commands.*;
import mapper.services.commands.input.InputProvider;
import mapper.services.links.ILinksFactory;
import mapper.services.links.RelativeLinksFactory;
import mapper.services.map.filler.MapFillersFactory;

import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        var pool = new ForkJoinPool();

        ILinksFactory linksFactory = new RelativeLinksFactory();
        LinksParser parser = new PageNavigationLinksParser(linksFactory);
        MapFillersFactory factory = new MapFillersFactory(parser, linksFactory, pool);

        var inputProvider = new InputProvider();
        var navigator = new Navigator(inputProvider);
        navigator.add(new SearchingLevelLimitCommand(inputProvider, factory))
                .add(new MaxArticlesCountCommand(inputProvider, factory))
                .add(new RequestDelayCommand(inputProvider, factory))
                .add(new SetParsingTypeCommand(inputProvider, factory))
                .add(new CreateMapCommand(inputProvider, factory))
                .add(new ExitCommand(navigator, pool))
                .start();
    }
}
