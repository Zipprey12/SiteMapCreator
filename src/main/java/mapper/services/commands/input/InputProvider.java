package mapper.services.commands.input;

import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Slf4j
public class InputProvider {

    private final Scanner scanner = new Scanner(System.in);

    public String getNext() {
        String input;
        try {
            input = scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            log.info("Входной поток закрыт, завершение работы");
            System.exit(0);
            return null;
        }
        return input;
    }

}
