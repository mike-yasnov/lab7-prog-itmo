package commands;

import auxiliary.Console;
import auxiliary.ExecutionResponse;
import managers.CollectionManager;
import models.Product;

/**
 * Команда 'min_by_part_number'. Вывести любой объект из коллекции, значение поля partNumber которого является минимальным.
 */
public class MinByPartNumber extends Command {
    private final CollectionManager collectionManager;

    public MinByPartNumber(CollectionManager collectionManager) {
        super("min_by_part_number", "вывести любой объект из коллекции, значение поля partNumber которого является минимальным");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String[] arguments,String login){
        if (!arguments[1].isEmpty()) return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        Product minProduct = null;
        for (var p : collectionManager.getCollection()) {
            if (minProduct == null || (p.getPartNumber() != null && p.getPartNumber().compareTo(minProduct.getPartNumber()) < 0)) {
                minProduct = p;
            }
        }
        if (minProduct == null) {
            return new ExecutionResponse("Продуктов не обнаружено.");
        } else {
            return new ExecutionResponse(minProduct.toString());
        }
    }
}
