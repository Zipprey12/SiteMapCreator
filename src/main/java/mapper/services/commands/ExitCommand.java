package mapper.services.commands;

import lombok.RequiredArgsConstructor;
import mapper.services.Navigator;

import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
public class ExitCommand implements Command {

    private final Navigator navigator;
    private final ForkJoinPool pool;

    @Override
    public String getDescription() {
        return "Выход";
    }

    @Override
    public void run() {
        navigator.stop();
        pool.shutdown();
    }
}
