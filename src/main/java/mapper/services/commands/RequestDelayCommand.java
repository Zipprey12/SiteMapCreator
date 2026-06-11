package mapper.services.commands;

import lombok.extern.slf4j.Slf4j;
import mapper.services.map.filler.MapFillersFactory;

import java.util.Scanner;

@Slf4j
public class RequestDelayCommand implements Command {

    private final Scanner scanner = new Scanner(System.in);
    private final MapFillersFactory factory;

    public RequestDelayCommand(MapFillersFactory factory) {
        this.factory = factory;
    }

    @Override
    public String getDescription() {
        return "Задержка между запросами";
    }

    @Override
    public void run() {
        printCurrent();
        boolean isPerforming = true;
        while (isPerforming) {
            if (tryInputDelay()) {
                isPerforming = false;
            }
        }
    }

    private void printCurrent() {
        log.info("Текущая задержка между запросами: {} мс", factory.getRequestDelayMs());
    }

    private boolean tryInputDelay() {
        log.info("Введите новое значение в миллисекундах или нажмите Enter для отмены:");
        var input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return true;
        }
        try {
            var value = Long.parseLong(input);
            if (value >= 0) {
                factory.setRequestDelayMs(value);
                log.info("Задержка установлена: {} мс", value);
                return true;
            }
        } catch (NumberFormatException e) {
            log.info("Введено некорректное значение");
            return false;
        }

        log.info("Значение не может быть отрицательным");
        return false;
    }
}