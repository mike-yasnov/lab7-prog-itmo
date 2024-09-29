package models;

import java.util.Objects;

/**
 * Класс, представляющий координаты объекта.
 */
public class Coordinates {
    private int x;
    private double y;

    /**
     * Конструктор класса Coordinates.
     * @param x Координата по оси X.
     * @param y Координата по оси Y.
     * @throws IllegalArgumentException Если координаты null.
     */
    public Coordinates(Integer x, Double y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Координаты не могут быть null");
        }
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    /**
     * Переопределение метода equals для сравнения координат.
     * @param obj Объект для сравнения.
     * @return true, если координаты равны, иначе false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinates that = (Coordinates) obj;
        return x == that.x && Double.compare(that.y, y) == 0;
    }
    /**
     * Переопределение метода hashCode для вычисления хэш-кода координат.
     * @return Хэш-код координат.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    /**
     * Переопределение метода toString для получения строкового представления координат.
     * @return Строковое представление координат.
     */
    @Override
    public String toString() {
        return x + ";" + y;
    }
}
