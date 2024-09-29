package commands;

import auxiliary.Console;
import auxiliary.ExecutionResponse;
import managers.CollectionManager;
import managers.Ask;
import managers.DBManager;
import models.Product;

/**
 * Команда 'add_if_max'. Добавляет новый элемент в коллекцию, если его значение меньше значение наименьшего элемента этой коллекции.
 */
public class AddIfMin extends Command {

    private final CollectionManager collectionManager;
    private final DBManager dbManager;

    public AddIfMin( CollectionManager collectionManager, DBManager dbManager) {
        super("add_if_min {element}", "добавить новый элемент в коллекцию, если его значение меньше значения наименьшего элемента этой коллекции");
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
    }

    /**
     * Выполняет команду.
     */
    @Override
    public ExecutionResponse apply(String[] arguments,String login){
        if (arguments[1].isEmpty())
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");

        Product product = Product.getFromString(arguments[1]);
        product.setId(dbManager.getFreeId());

        Product minProduct = collectionManager.getCollection().stream()
                .max(Product::compareTo)
                .orElse(null);

        if (minProduct == null || product.compareTo(minProduct) == 0) {
            if (!dbManager.insertProduct(product))
                return new ExecutionResponse(false, "При добавлении продукта возникла ошибка!");
            collectionManager.addToCollection(product);
            return new ExecutionResponse(true, "Продукт успешно добавлен!");
        } else {
            return new ExecutionResponse(false, "Продукт не добавлен, так как его значение не превышает максимальное значение в коллекции.");
        }

    }
}
