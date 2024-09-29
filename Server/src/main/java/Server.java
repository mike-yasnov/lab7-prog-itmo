import auxiliary.ExecutionResponse;
import commands.*;
import managers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.Record;

import java.net.SocketAddress;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class Server {
    static class ReadTask extends RecursiveAction {
        private final Record rec;
        private final CommandManager commandManager;
        private final NetworkManager networkManager;
        private final DBManager dbManager;

        public ReadTask(Record rec, CommandManager commandManager, NetworkManager networkManager, DBManager dbManager) {
            this.rec = rec;
            this.commandManager = commandManager;
            this.networkManager = networkManager;
            this.dbManager = dbManager;
        }

        @Override
        protected void compute() {
            Container commandd = NetworkManager.deserialize(rec.getArr());
            if (commandd != null) {
                ExecuteTask task = new ExecuteTask(commandd, rec.getAddr(), commandManager, networkManager, dbManager);
                task.fork();
                task.join();
            }
        }
    }

    static class ExecuteTask extends RecursiveAction {
        private final Container commandd;
        private final CommandManager commandManager;
        private final NetworkManager networkManager;
        private final DBManager dbManager;
        private final SocketAddress address;

        ExecuteTask(Container commandd, SocketAddress address, CommandManager
                commandManager, NetworkManager networkManager, DBManager dbManager) {
            this.commandd = commandd;
            this.commandManager = commandManager;
            this.networkManager = networkManager;
            this.dbManager = dbManager;
            this.address = address;
        }

        @Override
        protected void compute() {
            userCommand[0] = commandd.getCommandType().Type();
            userCommand[1] = commandd.getArgs();
            var command = commandManager.getCommands().get(userCommand[0]);
            ExecutionResponse response;
            if (userCommand[0].equals("")) response = new ExecutionResponse("");
            if (command == null)
                response = new ExecutionResponse(false, "Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");
            else {
                if (!userCommand[0].equals("login") & !userCommand[0].equals("register")) {
                    if (dbManager.exists(commandd.getLogin(), commandd.getPassword())) {
                        response = command.apply(userCommand, commandd.getLogin());
                    } else response = new ExecutionResponse(false, "Неверный логин или пароль");
                } else {
                    response = command.apply(userCommand, commandd.getLogin());
                }

            }
            logger.info("Команда обработана!");
            SendDataTask task;
            if (response != null) {
                task = new SendDataTask(response, address, networkManager);
                task.fork();

            } else {
                task = new SendDataTask(new ExecutionResponse(false, "Не удалось выполнить команду"), address, networkManager);
                task.fork();
            }
            task.join();
        }
    }

    static class SendDataTask extends RecursiveAction {
        private final ExecutionResponse response;
        private final SocketAddress address;
        private final NetworkManager networkManager;

        public SendDataTask(ExecutionResponse response, SocketAddress address, NetworkManager networkManager) {
            this.response = response;
            this.address = address;
            this.networkManager = networkManager;
        }

        @Override
        protected void compute() {
            byte[] bytes = NetworkManager.serializer(response);
            networkManager.sendData(new Record(bytes, address));
            logger.info("Отправлен ответ клиенту!");
        }
    }

    static ForkJoinPool pool = new ForkJoinPool();

    public static final Logger logger = LoggerFactory.getLogger(Server.class);
    static String[] userCommand = new String[2];
    static byte arr[] = new byte[5069];
    static int len = arr.length;

    public static void main(String[] args) {

        if (args.length == 0) {
            logger.error("Не введен файл конфигураии. Сервер не запущен!");
            System.exit(1);
        }
        DBManager dbManager = new DBManager(args);
        dbManager.connect();
        var collectionManager = new CollectionManager(dbManager);
        if (!collectionManager.loadCollection()) {
            System.exit(1);
        }
        var networkManager = new NetworkManager(17548, 800);
        while (!networkManager.init()) {
            logger.info("Менеджер сетевого взаимодействия инициализирован!");
        }
        var commandManager = new CommandManager() {{
            register("info", new Info(collectionManager));
            register("show", new Show(collectionManager));
            register("add", new Add(collectionManager, dbManager));
            register("clear", new Clear(collectionManager, dbManager));
            register("remove_lower", new RemoveLower(collectionManager, dbManager));
            register("min_by_part_number", new MinByPartNumber(collectionManager));
            register("filter_by_owner", new FilterByOwner(collectionManager));
            register("print_field_ascending_unit_of_measure", new PrintFieldAscendingUnitOfMeasure(collectionManager));
            register("update", new Update(collectionManager, dbManager));
            register("add_if_min", new AddIfMin(collectionManager, dbManager));
            register("remove_by_id", new RemoveById(collectionManager, dbManager));
            register("login", new Login(dbManager));
            register("register", new Register(dbManager));
            register("backup", new Backup(dbManager));
        }};
        run(networkManager, commandManager, dbManager);
    }

    public static void run(NetworkManager networkManager, CommandManager commandManager, DBManager dbManager) {
        while (true) {
            Record rec = networkManager.receiveData(len);
            if (rec != null) {
                ReadTask task = new ReadTask(rec, commandManager, networkManager, dbManager);
                pool.invoke(task);
            }
        }
    }
}

