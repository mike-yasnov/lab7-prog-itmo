package commands;

import auxiliary.Console;
import auxiliary.ExecutionResponse;

import java.util.List;

/**
 * Команда 'history'. Выводит последние 13 команд без аргументов.
 */
public class History extends Command {
    private final Console console;
    private final List<String> commandHistory;

    public History(Console console, List<String> commandHistory) {
        super("history", "Выводит последние 13 команд без аргументов");
        this.console = console;
        this.commandHistory=commandHistory;
    }

    /**
     * Выполняет команду.
     *
     * @param arguments аргументы команды (в данном случае они не используются).
     * @return Успешность выполнения команды и список последних 13 команд без аргументов.
     */
    @Override
    public ExecutionResponse apply(String[] arguments,String login){
        if (!arguments[1].isEmpty()) return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");


        int startIndex = Math.max(0, commandHistory.size() - 13); // Начинаем с конца списка, чтобы получить последние 13 команд
        List<String> last13Commands = commandHistory.subList(startIndex, commandHistory.size());

        StringBuilder output = new StringBuilder();
        for (String command : last13Commands) {
            // Разделяем команду и её аргументы и берём только саму команду (без аргументов)
            String[] parts = command.split("\\s+");
            output.append(parts[0]).append("\n");
        }

        return new ExecutionResponse(output.toString().trim());
    }
}

