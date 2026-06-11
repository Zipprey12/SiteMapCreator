package mapper.services.commands;

import lombok.extern.slf4j.Slf4j;
import mapper.model.SiteProtocol;
import mapper.repository.output.MapWriter;
import mapper.services.Navigator;
import mapper.services.links.LinksFactory;
import mapper.services.map.filler.MapFillersFactory;

import java.util.Scanner;

@Slf4j
public class CreateMapCommand implements Command {

    private final LinksFactory linksFactory;
    private final MapFillersFactory mapFillersFactory;
    private final Scanner scanner = new Scanner(System.in);

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
            log.warn("Не удалось подключиться по данной ссылке. Карта не была создана");
        } else {
            log.info("Происходит запись в файл. Не закрывайте приложение!");
            new MapWriter(map).write();
        }
    }

    private boolean tryInputUrl() {
        log.info("Введите адрес сайта: ");
        var input = scanner.nextLine();

        if (!linksFactory.trySetInitialParsingPage(input)) {
            log.warn("Введены некорректные данные!");
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
