package commands;

import auxiliary.Console;
import auxiliary.ExecutionResponse;
import managers.CollectionManager;
import managers.Ask;
import models.Person;
import models.Product;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда 'filter_by_owner'. Выводит элементы, значение поля owner которых равно заданному.
 */
public class FilterByOwner extends Command {
    private final CollectionManager collectionManager;

    public FilterByOwner( CollectionManager collectionManager) {
        super("filter_by_owner <OWNER>", "вывести элементы, значение поля owner которых равно заданному");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String[] arguments,String login){
        if (arguments[1].isEmpty()) {
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }

        Person owner = Person.getFromString(arguments[1]);
        List<Product> filteredProducts = filterByOwner(owner);

        if (filteredProducts.isEmpty()) {
            return new ExecutionResponse(false, "Продуктов с владельцем " + owner + " не обнаружено.");
        } else {
            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("Продукты с владельцем ").append(owner).append(":\n");
            filteredProducts.forEach(product -> responseBuilder.append(product).append("\n"));
            return new ExecutionResponse(true, responseBuilder.toString().trim());
        }

    }

    private List<Product> filterByOwner(Person owner) {
        return collectionManager.getCollection().stream()
                .filter(product -> product.getOwner().equals(owner))
                .collect(Collectors.toList());
    }
}
