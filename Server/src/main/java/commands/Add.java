package commands;

import auxiliary.Console;
import auxiliary.ExecutionResponse;
import managers.CollectionManager;
import managers.Ask;
import managers.DBManager;
import models.Product;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 */
public class Add extends Command {
    private final CollectionManager collectionManager;
    private final DBManager dbManager;

    public Add(CollectionManager collectionManager, DBManager dbManager) {
        super("add", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String[] arguments,String login){

        if (arguments[1].isEmpty())
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        Product p = Product.getFromString(arguments[1]);
        p.setId(dbManager.getFreeId());
        if (p != null && p.validate()) {
            if (!dbManager.insertProduct(p))
                return new ExecutionResponse(false, "При добавлении продукта возникла ошибка!");
            collectionManager.addToCollection(p);
            return new ExecutionResponse("Продукт успешно добавлено!");
        } else return new ExecutionResponse(false, "Поля Продукта не валидны! Продукт не создан!");

    }
}
