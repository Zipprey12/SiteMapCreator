package mapper.services;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mapper.services.commands.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Navigator {
    private final Scanner scanner = new Scanner(System.in);

    private final List<Runnable> commandsList = new ArrayList<>();
    private final HashMap<Runnable, String> commands = new HashMap<>();

    private boolean isRunning;
    @Setter
    private String description;

    public Navigator add(Command command) {
        commandsList.add(command);
        commands.put(command, command.getDescription());
        return this;
    }

    public Navigator add(Runnable runnable, String description) {
        commandsList.add(runnable);
        commands.put(runnable, description);
        return this;
    }

    public void start() {
        isRunning = true;
        while (isRunning) {
            runOnce();
        }
    }

    public void runOnce() {
        showCommands();
        var command = selectCommand();
        if (command == null) {
            log.warn("Введено некорректное значение!");
        } else {
            command.run();
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void showCommands() {
        log.info("\n");
        if (description != null) {
            log.info(description);
        }
        for (int i = 0; i < commands.size(); i++) {
            var runnable = commandsList.get(i);
            var commandDescription = commands.get(runnable);
            log.info("{} - {}", i + 1, commandDescription);
        }
    }

    private Runnable selectCommand() {
        log.info("[Ожидание ввода...]");
        var input = scanner.nextLine().trim().toLowerCase();
        try {
            int number = Integer.parseInt(input) - 1;
            return commandsList.get(number);
        } catch (Exception e) {
            return null;
        }
    }
}
