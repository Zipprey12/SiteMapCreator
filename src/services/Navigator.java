package services;

import services.commands.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Navigator {
    private final Scanner scanner = new Scanner(System.in);

    private final List<Runnable> commandsList = new ArrayList<>();
    private final HashMap<Runnable, String> commands = new HashMap<>();

    private boolean isRunning;
    private String description;

    public void setDescription(String description) {
        this.description = description;
    }

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
            System.out.println("Введено некорректнное значение!");
        } else {
            command.run();
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void showCommands() {
        System.out.println();

        if (description != null) {
            System.out.println(description);
        }
        for (int i = 0; i < commands.size(); i++) {
            var runnable = commandsList.get(i);
            var commandDescription = commands.get(runnable);
            System.out.println(i + 1 + " - " + commandDescription);
        }
    }

    private Runnable selectCommand() {
        System.out.println("[Ожидание ввода...]");
        var input = scanner.nextLine().trim().toLowerCase();
        try {
            int number = Integer.parseInt(input) - 1;
            return commandsList.get(number);
        } catch (Exception e) {
            return null;
        }
    }
}
