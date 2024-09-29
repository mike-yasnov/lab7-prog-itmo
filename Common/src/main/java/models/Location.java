package models;

import java.util.Objects;

public class Location {
    private long x;
    private Long y; // Поле не может быть null
    private String name; // Поле не может быть null

    public Location(long x, Long y, String name) {
        if (y == null || name == null) {
            throw new IllegalArgumentException("Поле y и name не может быть null");
        }
        this.x = x;
        this.y = y;
        this.name = name;
    }

    // Геттеры и сеттеры для доступа к полям класса
    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Поле name не может быть null");
        }
        this.name = name;
    }

    // Переопределение методов из класса Object

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x && Objects.equals(y, location.y) && Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, name);
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                ", name='" + name + '\'' +
                '}';
    }
}