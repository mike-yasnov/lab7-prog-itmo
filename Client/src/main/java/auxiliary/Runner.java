package auxiliary;

import commands.*;
import managers.Ask;
import managers.NetworkManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Runner {
    private String login;
    private String password;
    private boolean loggined = false;
    private Console console;
    private Map<CommandTypes, String[]> commands;
    private final List<String> scriptStack = new ArrayList<>();
    private int lengthRecursion = -1;
    private NetworkManager networkManager;
    private final List<String> commandHistory = new ArrayList<>();

    public Runner(NetworkManager networkManager, Console console, Map<CommandTypes, String[]> commands) {
        this.console = console;
        this.networkManager = networkManager;
        this.commands = commands;
    }


    public void interactiveMode() {
        try {

            ExecutionResponse commandStatus;

            String[] userCommand = {"", ""};

            while (true) {
                console.prompt();
                userCommand = (console.readln().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                //console.println(userCommand[0]);
                commandStatus = launchCommand(userCommand);
                if (commandStatus.getMassage() == "exit") break;
                console.println(commandStatus.getMassage());
            }
        } catch (NoSuchElementException exception) {
            console.printError("Пользовательский ввод не обнаружен!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
        }
    }

    private boolean checkRecursion(String argument, Scanner scriptScanner) {
        var recStart = -1;
        var i = 0;
        for (String script : scriptStack) {
            i++;
            if (argument.equals(script)) {
                if (recStart < 0) recStart = i;
                if (lengthRecursion < 0) {
                    console.selectConsoleScanner();
                    console.println("Была замечена рекурсия! Введите максимальную глубину рекурсии (0..500)");
                    while (lengthRecursion < 0 || lengthRecursion > 500) {
                        try {
                            console.print("> ");
                            lengthRecursion = Integer.parseInt(console.readln().trim());
                            if (lengthRecursion < 0 || lengthRecursion > 500) {
                                console.println("длина не распознана");
                            }
                        } catch (NumberFormatException e) {
                            console.println("длина не распознана");
                        }
                    }
                    console.selectFileScanner(scriptScanner);
                }
                if (i > recStart + lengthRecursion || i > 500)
                    return false;
            }
        }
        return true;
    }

    private ExecutionResponse scriptMode(String argument) {
        String[] userCommand = {"", ""};
        StringBuilder executionOutput = new StringBuilder();

        if (!new File(argument).exists()) return new ExecutionResponse(false, "Файл не существет!");
        if (!Files.isReadable(Paths.get(argument))) return new ExecutionResponse(false, "Прав для чтения нет!");

        scriptStack.add(argument);
        try (Scanner scriptScanner = new Scanner(new File(argument))) {

            ExecutionResponse commandStatus;

            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            console.selectFileScanner(scriptScanner);
            do {
                userCommand = (console.readln().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                while (console.isCanReadln() && userCommand[0].isEmpty()) {
                    userCommand = (console.readln().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                }
                executionOutput.append(console.getPrompt() + String.join(" ", userCommand) + "\n");
                var needLaunch = true;
                if (userCommand[0].equals("execute_script")) {
                    needLaunch = checkRecursion(userCommand[1], scriptScanner);
                }
                commandStatus = needLaunch ? launchCommand(userCommand) : new ExecutionResponse("Превышена максимальная глубина рекурсии");
                if (userCommand[0].equals("execute_script")) console.selectFileScanner(scriptScanner);
                executionOutput.append(commandStatus.getMassage() + "\n");
            } while (commandStatus.getExitCode() && !commandStatus.getMassage().equals("exit") && console.isCanReadln());

            console.selectConsoleScanner();
            if (!commandStatus.getExitCode() && !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())) {
                executionOutput.append("Проверьте скрипт на корректность введенных данных!\n");
            }
            return new ExecutionResponse(commandStatus.getExitCode(), executionOutput.toString());
        } catch (FileNotFoundException exception) {
            return new ExecutionResponse(false, "Файл со скриптом не найден!");
        } catch (NoSuchElementException exception) {
            return new ExecutionResponse(false, "Файл со скриптом пуст!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
            System.exit(0);
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        return new ExecutionResponse("");
    }

    private ExecutionResponse launchCommand(String[] userCommand) {
        ExecutionResponse response;
        if (userCommand[0].equals("")) return new ExecutionResponse("");
        var command = CommandTypes.getByString(userCommand[0]);
        if (loggined) {
            if (!commands.containsKey(command)) {
                command = null;
            }

            if (command == null)
                return new ExecutionResponse(false, "Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");

            switch (userCommand[0]) {

                case "execute_script" -> {
                    ExecutionResponse tmp = new ExecuteScript(console).apply(userCommand,login);
                    if (!tmp.getExitCode()) return tmp;
                    ExecutionResponse tmp2 = scriptMode(userCommand[1]);
                    return new ExecutionResponse(tmp2.getExitCode(), tmp.getMassage() + "\n" + tmp2.getMassage().trim());
                }
                default -> {
                    byte[] bytes = new byte[userCommand.length];
                    if (command == CommandTypes.ADD | command == CommandTypes.REMOVELOWER) {
                        try {
                            bytes = NetworkManager.serializer(new Container(login,password,command, Ask.askProduct(console, 0,login).getDataString()));
                        } catch (Ask.AskBreak e) {
                            return new ExecutionResponse("Отмена...");
                        }
                    } else if (command == CommandTypes.UPDATE) {
                        try {
                            bytes = NetworkManager.serializer(new Container(login,password,command, Ask.askProduct(console, Integer.parseInt(userCommand[1]),login).getDataString()));
                        } catch (Ask.AskBreak e) {
                            return new ExecutionResponse("Отмена...");
                        }
                    } else if (command == CommandTypes.HELP) {
                        console.println(new Help(console, commands).apply(userCommand,login).getMassage());
                    } else if (command == CommandTypes.EXIT) {
                        bytes = NetworkManager.serializer(new Container(login,password,CommandTypes.SAVE, ""));
                        networkManager.sendData(bytes);
                        return new Exit(console).apply(userCommand,login);
                    } else if (command == CommandTypes.REMOVEBYID) {
                        bytes = NetworkManager.serializer(new Container(login,password,command, userCommand[1]));
                    } else if (command == CommandTypes.ADDIFMIN) {
                        try {
                            bytes = NetworkManager.serializer(new Container(login,password,command, Ask.askProduct(console, 0,login).getDataString()));
                        } catch (Ask.AskBreak e) {
                            return new ExecutionResponse("Отмена...");
                        }
                    } else if (command == CommandTypes.HISTORY) {

                        console.println(new History(console, commandHistory).apply(userCommand,login).getMassage());
                    } else if (command == CommandTypes.FILTERBYOWNER) {
                        try {
                            bytes = NetworkManager.serializer(new Container(login,password,command, Ask.askOwner(console).getDataString()));
                        } catch (Ask.AskBreak e) {
                            return new ExecutionResponse("Отмена...");
                        }
                    } else {
                        bytes = NetworkManager.serializer(new Container(login,password,command, ""));
                    }
                    commandHistory.add(command.Type());
                    if (command != CommandTypes.HELP & command != CommandTypes.HISTORY) {
                        networkManager.sendData(bytes);
                        var data = networkManager.receiveData(5069);
                        response = NetworkManager.deserialize(data);
                        return response;
                    } else return new ExecutionResponse(false, "");
                }
            }
        }else {
            if (command == CommandTypes.LOGIN) {
                try {
                    if(!userCommand[1].isEmpty()) return new ExecutionResponse(false,"Неправильное количество аргументов! \nИспользование: 'login'");
                    console.println("*Авторизация*");
                    console.print("Логин:");
                    login = console.readln().trim();
                    console.print("Пароль:");
                    password = console.readln().trim();
                    MessageDigest sha384 = MessageDigest.getInstance("SHA-384");
                    var info= sha384.digest(password.getBytes());
                    StringBuilder builder = new StringBuilder();
                    for(var b: info){
                        builder.append(String.format("%02X", b));
                    }
                    password = builder.toString();
                    var bytes = NetworkManager.serializer(new Container(login, password, command, login + ";" + password));
                    networkManager.sendData(bytes);
                    var data = networkManager.receiveData(5069);
                    response = NetworkManager.deserialize(data);
                    if (response.getExitCode() == true) {
                        loggined = true;
                    }
                    return response;
                }
                catch (NoSuchAlgorithmException e) {
                    return new ExecutionResponse(false,"Возникла ошибка авторизации!");
                }
            }
            else if(command==CommandTypes.REGISTER){
                try {
                    if(!userCommand[1].isEmpty()) return new ExecutionResponse(false,"Неправильное количество аргументов! \n Использование: 'register'");
                    console.println("*Регистрация*");
                    console.print("Логин:");
                    login = console.readln().trim();
                    console.print("Пароль:");
                    password = console.readln().trim();
                    MessageDigest sha384 = MessageDigest.getInstance("SHA-384");
                    var info= sha384.digest(password.getBytes());
                    StringBuilder builder = new StringBuilder();
                    for(var b: info){
                        builder.append(String.format("%02X", b));
                    }
                    password = builder.toString();
                    var bytes = NetworkManager.serializer(new Container(login, password, command, login + ";" + password));
                    networkManager.sendData(bytes);
                    var data = networkManager.receiveData(5069);
                    response = NetworkManager.deserialize(data);
                    if (response.getExitCode() == true) {
                        loggined = true;
                    }
                    return response;
                } catch (NoSuchAlgorithmException e) {
                    return new ExecutionResponse(false,"Возникла ошибка регистрации!");
                }
            }
            else return new ExecutionResponse(false, "Вы не авторизованы! Авторизуйтесь или зарегистрируйтесь(login/register)");
        }
    }
}
