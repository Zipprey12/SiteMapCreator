package mapper.services.commands;

import lombok.extern.slf4j.Slf4j;
import mapper.services.map.filler.MapFillersFactory;

import java.util.Optional;

@Slf4j
public class SearchingLevelLimitCommand extends CommandWithInput<Integer> {

    private final MapFillersFactory factory;

    public SearchingLevelLimitCommand(MapFillersFactory factory) {
        this.factory = factory;
    }

    @Override
    public String getDescription() {
        return "Лимит вложенности поиска";
    }

    @Override
    public void run() {
        printCurrent();
        super.run();
    }

    @Override
    protected String getOperationDescription() {
        return "Введите новое значение или нажмите Enter для отмены:";
    }

    @Override
    protected Optional<Integer> tryParseValue(String value) {
        try {
            var parsed = Integer.parseInt(value);
            if (parsed >= 0) {
                return Optional.of(parsed);
            }
            log.info("Значение не может быть отрицательным числом");
        } catch (Exception e) {
            log.info("Введено некорректное значение");
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    protected void executeWithValue(Integer value) {
        factory.setMaxSearchingLevel(value);
    }

    private void printCurrent() {
        log.info("Текущий лимит вложенности поиска: {}", factory.getMaxSearchingLevel());
    }
}
