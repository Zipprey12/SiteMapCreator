package mapper.services.commands;

import lombok.extern.slf4j.Slf4j;
import mapper.model.ParsingType;
import mapper.services.commands.input.InputProvider;
import mapper.services.map.filler.MapFillersFactory;

import java.util.Optional;

@Slf4j
public class SetParsingTypeCommand extends CommandWithInput<ParsingType> {

    public static final String INCORRECT_VALUE_MESSAGE = "Введено некорректное значение";

    private final MapFillersFactory factory;

    public SetParsingTypeCommand(InputProvider inputProvider, MapFillersFactory factory) {
        super(inputProvider);
        this.factory = factory;
    }

    @Override
    public String getDescription() {
        return "Задать режим парсинга сайта";
    }

    @Override
    protected String getOperationDescription() {
        return """
                Введите номер типа парсинга. Доступные:
                1 - Ссылки, исключая параметры (быстрый)
                2 - Полный, включая параметры в ссылках
                Для отмены нажмите Enter""";
    }

    @Override
    protected Optional<ParsingType> tryParseValue(String value) {
        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (Exception e) {
            log.warn(INCORRECT_VALUE_MESSAGE);
            return Optional.empty();
        }

        if (parsed == 1) {
            return Optional.of(ParsingType.FAST);
        } else if (parsed == 2) {
            return Optional.of(ParsingType.EXTENDED);
        }
        log.warn(INCORRECT_VALUE_MESSAGE);
        return Optional.empty();
    }

    @Override
    protected void executeWithValue(ParsingType value) {
        factory.setParsingType(value);
    }
}

