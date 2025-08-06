package services.commands;

import services.Navigator;

public class ExitCommand implements Command{
    private final Navigator navigator;

    public ExitCommand(Navigator navigator){
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
