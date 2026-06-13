package mapper.services.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mapper.services.commands.input.InputProvider;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class CommandWithInput<T> implements Command {

    @Getter(AccessLevel.PROTECTED)
    private final InputProvider inputProvider;

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
        String input;

        input = inputProvider.getNext();
        if (input == null || input.isEmpty()) {
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
