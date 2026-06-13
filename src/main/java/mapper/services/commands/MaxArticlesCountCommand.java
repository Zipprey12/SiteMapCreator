package mapper.services.commands;

import lombok.extern.slf4j.Slf4j;
import mapper.services.commands.input.InputProvider;
import mapper.services.map.filler.MapFillersFactory;

import java.util.Optional;

@Slf4j
public class MaxArticlesCountCommand extends CommandWithInput<Integer> {

    private final MapFillersFactory factory;

    public MaxArticlesCountCommand(InputProvider inputProvider,
                                   MapFillersFactory factory) {
        super(inputProvider);
        this.factory = factory;
    }

    @Override
    public String getDescription() {
        return "Максимальное количество страниц";
    }

    @Override
    public void run() {
        log.info("Текущий лимит страниц: {}", factory.getMaxArticlesCount());
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
            if (parsed > 0) {
                return Optional.of(parsed);
            }
            log.info("Значение должно быть больше нуля");
        } catch (Exception e) {
            log.info("Введено некорректное значение");
        }
        return Optional.empty();
    }

    @Override
    protected void executeWithValue(Integer value) {
        factory.setMaxArticlesCount(value);
    }
}