package commands;

import auxiliary.Console;
import auxiliary.ExecutionResponse;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Команда 'help'. Выводит справку по доступным командам
 */
public class Help extends Command {
    private final Console console;
    private final Map<CommandTypes, String[]> commands;

    public Help(Console console, Map<CommandTypes, String[]> commands) {
        super("help", "вывести справку по доступным командам");
        this.console = console;
        this.commands = commands;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String[] arguments,String login){
        if (!arguments[1].isEmpty()) return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");

        return new ExecutionResponse(commands.keySet().stream().map(command -> String.format(" %-35s%-1s%n", commands.get(command)[0], commands.get(command)[1])).collect(Collectors.joining("\n")));
    }
}
