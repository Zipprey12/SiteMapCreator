package mapper.services.commands;

import lombok.extern.slf4j.Slf4j;
import mapper.model.SiteProtocol;
import mapper.repository.output.MapWriter;
import mapper.services.Navigator;
import mapper.services.commands.input.InputProvider;
import mapper.services.links.ILinksFactory;
import mapper.services.map.filler.MapFillersFactory;

import java.util.Optional;

@Slf4j
public class CreateMapCommand extends CommandWithInput<String> {

    private final ILinksFactory linksFactory;
    private final MapFillersFactory mapFillersFactory;

    private Navigator navigator;

    public CreateMapCommand(InputProvider provider, MapFillersFactory mapFillersFactory) {
        super(provider);
        this.linksFactory = mapFillersFactory.getLinksFactory();
        this.mapFillersFactory = mapFillersFactory;
    }

    @Override
    public String getDescription() {
        return "Создать карту сайта";
    }

    @Override
    protected String getOperationDescription() {
        return "Введите адрес сайта:";
    }

    @Override
    protected Optional<String> tryParseValue(String value) {
        return Optional.ofNullable(value);
    }

    @Override
    protected void executeWithValue(String value) {
        if (!trySetInputUrl(value)) {
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

    private boolean trySetInputUrl(String url) {
        if (!linksFactory.trySetInitialParsingPage(url)) {
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
        navigator = new Navigator(getInputProvider());
        navigator.setDescription("Выберите протокол подключения");

        navigator.add(() -> linksFactory.setProtocol(SiteProtocol.HTTPS), "https")
                .add(() -> linksFactory.setProtocol(SiteProtocol.HTTP), "http");
    }
}
