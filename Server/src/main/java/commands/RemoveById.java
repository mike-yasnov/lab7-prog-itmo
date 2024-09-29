package commands;

import auxiliary.Console;
import auxiliary.ExecutionResponse;
import managers.CollectionManager;
import managers.DBManager;

/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции.
 */
public class RemoveById extends Command {

    private final CollectionManager collectionManager;
    private final DBManager dbManager;
    public RemoveById( CollectionManager collectionManager, DBManager dbManager) {
        super("remove_by_id <ID>", "удалить элемент из коллекции по ID");

        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String[] arguments,String login){
        if (arguments[1].isEmpty())
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        int id = -1;
        try {
            id = Integer.parseInt(arguments[1].trim());
        } catch (NumberFormatException e) {
            return new ExecutionResponse(false, "ID не распознан");
        }


        if (collectionManager.getById(id) == null || !collectionManager.getCollection().contains(collectionManager.getById(id)))
            return new ExecutionResponse(false, "Не существующий ID");
        if(!collectionManager.getById(id).getCreator().equals(login)) return new ExecutionResponse(false,"У вас нет прав для удаления данного элемента");
        if (!dbManager.deleteById(id))
            return new ExecutionResponse(false, "При удалении продукта возникла ошибка");
        collectionManager.removeFromCollection(collectionManager.getById(id));


        return new ExecutionResponse("Продукт успешно удален!");
    }
}