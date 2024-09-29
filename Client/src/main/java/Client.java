import auxiliary.StandardConsole;
import auxiliary.Runner;
import commands.CommandTypes;
import managers.NetworkManager;

import java.util.HashMap;
import java.util.Map;

public class Client {
    public static void main(String[] args) {
        var console = new StandardConsole();
        NetworkManager networkManager = new NetworkManager(8000);

        while (!networkManager.init(args)) {}
        Map<CommandTypes,String[]> commands = new HashMap<>();
        commands.put(CommandTypes.HELP, new String[]{"help", "вывести справку по доступным командам"});
        commands.put(CommandTypes.ADD,new String[]{"add {element}", "добавить новый элемент в коллекцию"});
        commands.put(CommandTypes.ADDIFMIN, new String[]{"add_if_min {element}", "добавить новый элемент в коллекцию, если его значение меньше значения наименьшего элемента этой коллекции"});
        commands.put(CommandTypes.CLEAR,new String[]{"clear", "очистить коллекцию"});
        commands.put(CommandTypes.FILTERBYOWNER, new String[]{"filter_by_owner <OWNER>", "вывести элементы, значение поля owner которых равно заданному"});
        commands.put(CommandTypes.HISTORY, new String[]{"history", "Выводит последние 13 команд без аргументов"});
        commands.put(CommandTypes.INFO, new String[]{"info", "вывести информацию о коллекции"});
        commands.put(CommandTypes.MINBYPARTNUMBER, new String[]{"min_by_part_number", "вывести любой объект из коллекции, значение поля partNumber которого является минимальным"});
        commands.put(CommandTypes.PRINTFIELDASCENDINGUNITOFMEASURE, new String[]{"print_field_ascending_unit_of_measure", "вывести значения поля unitOfMeasure всех элементов в порядке возрастания"});
        commands.put(CommandTypes.REMOVEBYID,new String[]{"remove_by_id <ID>", "удалить элемент из коллекции по ID"});
        commands.put(CommandTypes.REMOVELOWER,new String[]{"remove_lower {element}", "удалить из коллекции все элементы, меньшие, чем заданный"});
        commands.put(CommandTypes.SHOW, new String[]{"show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"});
        commands.put(CommandTypes.UPDATE, new String[]{"update <ID> {element}", "обновить значение элемента коллекции по ID"});
        commands.put(CommandTypes.EXIT,new String[]{"exit", "завершить программу (без сохранения в файл)"});
        commands.put(CommandTypes.EXECUTESCRIPT,new String[]{"execute_script <file_name>", "исполнить скрипт из указанного файла"});
        new Runner(networkManager,console, commands).interactiveMode();

    }
}
