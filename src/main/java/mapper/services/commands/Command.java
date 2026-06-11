package mapper.services.commands;

public interface Command extends Runnable {
    String getDescription();
}
