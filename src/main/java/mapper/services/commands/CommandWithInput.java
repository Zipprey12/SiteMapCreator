package mapper.services.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Scanner;

@Slf4j
public abstract class CommandWithInput<T> implements Command {

    @Getter(AccessLevel.PROTECTED)
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        boolean isPerforming = true;
        while (isPerforming) {
            if (tryExecute()) {
                isPerforming = false;
            }
        }
    }

    protected abstract String getOperationDescription();

    protected abstract Optional<T> tryParseValue(String value);

    protected abstract void executeWithValue(T value);

    protected boolean tryExecute() {
        log.info(getOperationDescription());
        var input = getScanner().nextLine().trim();
        if (input.isEmpty()) {
            return true;
        }

        var parsed = tryParseValue(input);
        if (parsed.isEmpty()) {
            return false;
        }
        executeWithValue(parsed.get());
        return true;
    }

}
