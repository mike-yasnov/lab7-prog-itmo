package commands;

import auxiliary.ExecutionResponse;
import managers.DBManager;

/**
 * Команда 'backup'. Создает резервную копию базы данных.
 */
public class Backup extends Command {
    private final DBManager dbManager;

    public Backup(DBManager dbManager) {
        super("backup", "создать резервную копию базы данных");
        this.dbManager = dbManager;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String[] arguments, String login) {
        if (arguments.length < 2 || arguments[1].isEmpty()) {
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + " <backupFilePath>'");
        }

        String backupFilePath = arguments[1].trim();
        boolean success = dbManager.backupDatabase(backupFilePath);

        if (success) {
            return new ExecutionResponse(true, "Резервная копия базы данных успешно создана!");
        } else {
            return new ExecutionResponse(false, "Ошибка при создании резервной копии базы данных!");
        }
    }
}
