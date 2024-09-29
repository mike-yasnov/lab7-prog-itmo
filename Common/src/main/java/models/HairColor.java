package models;

public enum HairColor {
    GREEN,
    RED,
    BLACK,
    WHITE;
    /**
     * @return Строка со всеми элементами enum'а через запятую.
     */
    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var dragonType : values()) {
            nameList.append(dragonType.name()).append(", ");
        }
        return nameList.substring(0, nameList.length()-2);
    }
}
