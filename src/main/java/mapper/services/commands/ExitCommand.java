package mapper.services.commands;

import mapper.services.Navigator;

public class ExitCommand implements Command {
    private final Navigator navigator;

    public ExitCommand(Navigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public String getDescription() {
        return "Выход";
    }

    @Override
    public void run() {
        navigator.stop();
    }
}
