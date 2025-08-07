package services.commands;

import model.SiteProtocol;
import repository.output.MapWriter;
import services.Navigator;
import services.links.LinksFactory;
import services.map.filler.MapFillersFactory;

import java.util.Scanner;

public class CreateMapCommand implements Command {

    private final LinksFactory linksFactory;
    private final MapFillersFactory mapFillersFactory;

    private Navigator navigator;

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
            System.out.println("Не удалось подклучиться по данной ссылке. Карта не была создана");
        } else {
            System.out.println("Происходит запись в файл. Не закрывайте приложение!");
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
        return linksFactory.getProtocol() != null || tryInputProtocol();
    }

    private boolean tryInputProtocol() {
        if (navigator == null) {
            initializeNavigator();
        }
        navigator.runOnce();
        return linksFactory.getProtocol() != null;
    }

    private void initializeNavigator() {
        navigator = new Navigator();
        navigator.setDescription("Выберите протокол подключения");

        navigator.add(() -> linksFactory.setProtocol(SiteProtocol.HTTPS), "https")
                .add(() -> linksFactory.setProtocol(SiteProtocol.HTTP), "http")
                .add(() -> linksFactory.setProtocol(null), "отмена");
    }
}
