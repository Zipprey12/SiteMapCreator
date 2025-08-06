package services.commands;

import model.SiteProtocol;
import repository.output.MapWriter;
import services.Navigator;
import services.links.LinksFactory;
import services.map.MapFillersFactory;

import java.util.Scanner;

public class CreateMapCommand implements Command {

    private final LinksFactory linksFactory;
    private final MapFillersFactory mapFillersFactory;

    private Navigator navigator;
    private SiteProtocol protocol = null;

    public CreateMapCommand(MapFillersFactory mapFillersFactory) {
        this.linksFactory = mapFillersFactory.getLinksFactory();
        this.mapFillersFactory = mapFillersFactory;
    }

    @Override
    public String getDescription() {
        return "Создать карту сайта";
    }

    @Override
    public void run() {
        if (!tryInputUrl()) {
            return;
        }
        var map = mapFillersFactory.createMap();
        if (map == null) {
            System.out.println("Не удалось подклучиться. Карта не была создана");
        } else {
            new MapWriter(map).write();
        }
    }

    private boolean tryInputUrl() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите адрес сайта: ");
        var input = scanner.nextLine();

        if (!linksFactory.trySetInitialParsingPage(input)) {
            System.out.println("Введены некорректные данные!");
            return false;
        }

        if (linksFactory.getProtocol() != null || tryInputProtocol()) {
            linksFactory.setProtocol(protocol);
            return true;
        }

        return false;
    }

    private boolean tryInputProtocol() {
        if (navigator == null) {
            initializeNavigator();
        }
        navigator.runOnce();
        return protocol != null;
    }

    private void initializeNavigator() {
        navigator = new Navigator();
        navigator.setDescription("Выберите протокол подключения");

        navigator.add(() -> protocol = SiteProtocol.HTTPS, "https")
                .add(() -> protocol = SiteProtocol.HTTP, "http")
                .add(() -> protocol = null, "отмена");
    }
}
