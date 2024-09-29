package models;

public enum UnitOfMeasure {
    KILOGRAMS,
    CENTIMETERS,
    MILLILITERS,
    MILLIGRAMS;
    /**
     * @return Строка со всеми элементами enum'а через запятую.
     */
    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var dragonCharacterType : values()) {
            nameList.append(dragonCharacterType.name()).append(", ");
        }
        return nameList.substring(0, nameList.length()-2);
    }}
