package mapper.services.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mapper.services.map.filler.MapFillersFactory;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RequestDelayCommand extends CommandWithInput<Long> {

    private final MapFillersFactory factory;

    @Override
    public String getDescription() {
        return "Задержка между запросами";
    }

    @Override
    public void run() {
        printCurrent();
        super.run();
    }

    @Override
    protected String getOperationDescription() {
        return "Введите новое значение в миллисекундах или нажмите Enter для отмены:";
    }

    @Override
    protected Optional<Long> tryParseValue(String value) {
        try {
            var parsed = Long.parseLong(value);
            if (parsed >= 0) {
                return Optional.of(parsed);
            }
        } catch (NumberFormatException e) {
            log.info("Введено некорректное значение");
            return Optional.empty();
        }
        log.info("Задержка не может быть отрицательным числом");
        return Optional.empty();
    }

    @Override
    protected void executeWithValue(Long value) {
        factory.setRequestDelayMs(value);
        log.info("Задержка установлена: {} мс", value);
    }

    private void printCurrent() {
        log.info("Текущая задержка между запросами: {} мс", factory.getRequestDelayMs());
    }
}