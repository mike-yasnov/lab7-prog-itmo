package managers;

import models.Product;

import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Менеджер коллекции, отвечающий за управление коллекцией продуктов.
 */
public class CollectionManager {

    private TreeSet<Product> collection = new TreeSet<>();



    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private final DBManager dbManager;

    /**
     * Конструктор класса CollectionManager.
     * @param saveManager менеджер сохранения и загрузки коллекции
     */
    public CollectionManager(DBManager dbManager) {
        this.lastInitTime = null;
        this.lastSaveTime = null;
        this.dbManager = dbManager;
    }





    /**
     * Получить коллекцию продуктов.
     * @return коллекция продуктов
     */
    public synchronized TreeSet<Product> getCollection() {
        return collection;
    }
    /**
     * Получить время последней инициализации коллекции.
     * @return время последней инициализации коллекции
     */
    public synchronized LocalDateTime getLastInitTime() {
        return lastInitTime;
    }
    /**
     * Получить время последнего сохранения коллекции.
     * @return время последнего сохранения коллекции
     */
    public synchronized LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }
    /**
     * Получить тип коллекции.
     * @return тип коллекции
     */
    public synchronized String collectionType() {
        return collection.getClass().getName();
    }
    /**
     * Получить размер коллекции.
     * @return размер коллекции
     */
    public synchronized int collectionSize() {
        return collection.size();
    }
    /**
     * Получить первый элемент коллекции.
     * @return первый элемент коллекции
     */
    public synchronized Product getFirst() {
        return collection.first();
    }

    /**
     * Получить продукт по его идентификатору.
     * @param id идентификатор продукта
     * @return продукт с указанным идентификатором или null, если такого продукта нет в коллекции
     */
    public synchronized Product getById(int id) {
        return collection.stream()
                .filter(product -> product.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Проверить наличие продукта в коллекции по его идентификатору.
     * @param id идентификатор продукта
     * @return true, если продукт с указанным идентификатором существует в коллекции, в противном случае - false
     */
    public synchronized boolean checkExist(int id) {
        return collection.stream()
                .anyMatch(product -> product.getId() == id);
    }
    /**
     * Получить продукт из коллекции по его значению.
     * @param elementToFind объект продукта для поиска
     * @return найденный продукт или null, если продукт не найден
     */
    public synchronized Product getByValue(Product elementToFind) {
        return collection.stream()
                .filter(element -> element.equals(elementToFind))
                .findFirst()
                .orElse(null);
    }
    /**
     * Добавить продукт в коллекцию.
     * @param element добавляемый продукт
     */
    public synchronized void addToCollection(Product element) {
        collection.add(element);
    }
    /**
     * Удалить продукт из коллекции.
     * @param element удаляемый продукт
     */
    public synchronized void removeFromCollection(Product element) {
        collection.remove(element);

    }
    /**
     * Очистить коллекцию.
     */
    public synchronized void clearCollection() {
        collection.clear();
    }

    /**
     * Загрузить коллекцию.
     * @return true, если коллекция успешно загружена, в противном случае - false
     */
    public synchronized boolean loadCollection() {
        dbManager.loadCollection(collection);
        lastInitTime = LocalDateTime.now();
        return true;
    }


    /**
     * Представить коллекцию в виде строки.
     * @return строковое представление коллекции
     */
    @Override
    public synchronized String toString() {
        if (collection.isEmpty()) {
            return "Коллекция пуста!";
        }
        StringBuilder info = new StringBuilder();
        for (Product product : collection) {
            info.append(product).append("\n");
        }
        return info.toString();
    }



}
