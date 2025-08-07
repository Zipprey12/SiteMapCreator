import repository.parsers.LinksParser;
import repository.parsers.PageNavigationLinksParser;
import services.Navigator;
import services.commands.CreateMapCommand;
import services.commands.ExitCommand;
import services.commands.SearchingLevelLimitCommand;
import services.links.LinksFactory;
import services.links.RelativeLinksFactory;
import services.map.MapFillersFactory;

public class Main {
    public static void main(String[] args) {
        LinksFactory linksFactory = new RelativeLinksFactory();
        LinksParser parser = new PageNavigationLinksParser(linksFactory);
        MapFillersFactory factory = new MapFillersFactory(parser, linksFactory);

        Navigator navigator = new Navigator();
        navigator.add(new SearchingLevelLimitCommand(factory))
                .add(new CreateMapCommand(factory))
                .add(new ExitCommand(navigator))
                .start();
    }
}
