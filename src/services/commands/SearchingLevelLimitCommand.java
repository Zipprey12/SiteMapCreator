package services.commands;

import services.map.MapFillersFactory;

import java.util.Scanner;

public class SearchingLevelLimitCommand implements Command {

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
        System.out.println("Текущий лимит вложенности поиска: " + factory.getMaximumSearchingLevel());
    }

    private boolean tryInputLevel() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите новое значение или нажмите Enter для отмены:");
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
            System.out.println("Введено некорректное значение");
            return false;
        }

        System.out.println("Значение не может быть отрицательным числом");
        return false;
    }
}
