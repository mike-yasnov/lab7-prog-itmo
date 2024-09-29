package commands;

import auxiliary.Console;
import auxiliary.ExecutionResponse;
import managers.CollectionManager;
import managers.DBManager;
import models.Product;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Команда 'clear'. Очищает коллекцию.
 */
public class Clear extends Command {
    private final CollectionManager collectionManager;
    private final DBManager dbManager;

    public Clear(CollectionManager collectionManager, DBManager dbManager) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
        commandType = CommandTypes.CLEAR;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String[] arguments, String login) {
        if (!arguments[1].isEmpty())
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");

        Iterator<Product> iterator = collectionManager.getCollection().iterator();
        CopyOnWriteArrayList<Product> elements = new CopyOnWriteArrayList<>();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if(product.getCreator().equals(login)) elements.add(product);

        }
        for (var e : elements) {
            collectionManager.removeFromCollection(e);
        }
        dbManager.clear(login);

        return new ExecutionResponse("Коллекция очищена!");
    }
}
