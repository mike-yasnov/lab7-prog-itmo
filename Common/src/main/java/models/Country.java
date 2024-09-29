package models;

public enum Country {
    UNITED_KINGDOM,
    FRANCE,
    CHINA,
    SOUTH_KOREA;
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
