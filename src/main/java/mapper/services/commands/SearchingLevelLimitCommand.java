package mapper.services.commands;

import lombok.extern.slf4j.Slf4j;
import mapper.services.map.filler.MapFillersFactory;

import java.util.Scanner;

@Slf4j
public class SearchingLevelLimitCommand implements Command {

    private final Scanner scanner = new Scanner(System.in);
    private final MapFillersFactory factory;
    private int level;

    public SearchingLevelLimitCommand(MapFillersFactory factory) {
        this.factory = factory;
        level = factory.getMaximumSearchingLevel();
    }

    @Override
    public String getDescription() {
        return "Лимит вложенности поиска";
    }

    @Override
    public void run() {
        printCurrent();
        boolean isPerforming = true;
        while (isPerforming) {
            if (tryInputLevel()) {
                factory.setMaximumSearchingLevel(level);
                isPerforming = false;
            }
        }
    }

    private void printCurrent() {
        log.info("Текущий лимит вложенности поиска: {}", factory.getMaximumSearchingLevel());
    }

    private boolean tryInputLevel() {
        log.info("Введите новое значение или нажмите Enter для отмены:");
        var input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return true;
        }
        try {
            var value = Integer.parseInt(input);
            if (value >= 0) {
                level = value;
                return true;
            }
        } catch (Exception e) {
            log.info("Введено некорректное значение");
            return false;
        }

        log.info("Значение не может быть отрицательным числом");
        return false;
    }
}
