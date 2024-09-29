package commands;

import auxiliary.ExecutionResponse;
import managers.CollectionManager;
import managers.DBManager;
import models.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Команда для удаления из коллекции всех элементов, меньших, чем заданный.
 */
public class RemoveLower extends Command {
    private final CollectionManager collectionManager;
    private final DBManager dbManager;

    /**
     * Конструктор класса RemoveLower.
     *
     * @param console           Консоль для ввода/вывода.
     * @param collectionManager Менеджер коллекции.
     */
    public RemoveLower(CollectionManager collectionManager, DBManager dbManager) {
        super("remove_lower {element}", "удалить из коллекции все элементы, меньшие, чем заданный");
        this.collectionManager = collectionManager;
        this.dbManager = dbManager;
    }

    /**
     * Метод применения команды RemoveLower.
     *
     * @param arguments Аргументы команды.
     * @return Результат выполнения команды.
     */
    @Override
    public ExecutionResponse apply(String[] arguments,String login){
        if (arguments[1].isEmpty())
            return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");

        Product elementToRemove = null;
        elementToRemove = Product.getFromString(arguments[1]);
        elementToRemove.setId(dbManager.getFreeId());
        if (elementToRemove == null) {
            return new ExecutionResponse(false, "Невозможно разобрать элемент");
        }

        Product finalElementToRemove = elementToRemove;
        Iterator<Product> iterator = collectionManager.getCollection().iterator();
        List<Product> elements = new ArrayList<>();
        while (iterator.hasNext()) {
            var e = iterator.next();
            if (e.getId() == finalElementToRemove.getId()) break;
            else {
                if (e.getCreator().equals(login))
                    elements.add(e);
            }
        }
        for (var e : elements) {
            if (!dbManager.deleteById(e.getId()))
                return new ExecutionResponse(false, "При удалении продукта возникла ошибка");
            collectionManager.removeFromCollection(e);
        }

        return new ExecutionResponse("Элементы, меньшие, чем заданный, успешно удалены");
    }
}
