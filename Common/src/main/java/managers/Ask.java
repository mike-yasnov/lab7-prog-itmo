package managers;

import auxiliary.Console;
import models.*;

import java.util.NoSuchElementException;

public class Ask {
    public static class AskBreak extends Exception {}

    public static Product askProduct(Console console, int id,String login) throws AskBreak {
        try {
            String name;
            while (true) {
                console.print("name: ");
                name = console.readln().trim();
                if (name.equals("exit")) throw new AskBreak();
                if (!name.isEmpty()) break;
            }
            var coordinates = askCoordinates(console);
            var price = askFloat(console, "price");
            var partNumber = askStringInRange(console, "partNumber", 27, 42, true);
            var manufactureCost = askFloat(console, "manufactureCost");

            var unitOfMeasure = askUnitOfMeasure(console);
            var owner = askOwner(console);
            return new Product(id, name, coordinates, java.time.LocalDateTime.now(), price, partNumber, manufactureCost, unitOfMeasure, owner,login);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.println("Ошибка чтения");
            return null;
        }
    }

    public static Coordinates askCoordinates(Console console) throws AskBreak {
        try {
            int x;
            while (true) {
                console.print("coordinates.x: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.equals("")) {
                    try {
                        x = Integer.parseInt(line);
                        if (x > 754) {
                            console.printError("Максимальное значение поля x: 754");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                        console.printError("Неверный формат числа");
                    }
                }
            }
            double y;
            while (true) {
                console.print("coordinates.y: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.equals("")) {
                    try {
                        y = Double.parseDouble(line);
                        break;
                    } catch (NumberFormatException e) {
                    console.printError("Неверный формат числа");
                    }
                }
            }
            return new Coordinates(x, y);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static Float askFloat(Console console, String fieldName) throws AskBreak {
        try {
            float value;
            while (true) {
                console.print(fieldName + ": ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.equals("")) {
                    try {
                        value = Float.parseFloat(line);
                        if (value <= 0) {
                            console.printError("Значение должно быть больше нуля");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                        console.printError("Неверный формат числа");
                    }
                }
            }
            return value;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static String askStringInRange(Console console, String fieldName, int minLength, int maxLength, boolean allowNull) throws AskBreak {
        try {
            console.print(fieldName + ": ");
            while (true) {
                String input = console.readln().trim();
                if (input.equals("exit")) throw new AskBreak();
                if (allowNull && input.isEmpty()) return null;
                if (input.length() >= minLength && input.length() <= maxLength) return input;
                console.printError("Значение поля " + fieldName + " должно быть строкой от " + minLength + " до " + maxLength + " символов включительно");
                console.print(fieldName + ": ");
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static UnitOfMeasure askUnitOfMeasure(Console console) throws AskBreak {
        try {
            console.print("UnitOfMeasure (" + UnitOfMeasure.names() + "): ");
            UnitOfMeasure unitOfMeasure;
            while (true) {
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        unitOfMeasure = UnitOfMeasure.valueOf(line.toUpperCase());
                        break;
                    } catch (IllegalArgumentException e) {
                        console.printError("Неверный формат единицы измерения");
                    }
                } else return null;
            }
            return unitOfMeasure;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static Person askOwner(Console console) throws AskBreak {
        try {
            String ownerName;
            while (true) {
                console.print("owner.name: ");
                ownerName = console.readln().trim();
                if (ownerName.equals("exit")) throw new AskBreak();
                if (!ownerName.isEmpty()) break;
            }

            float ownerHeight;
            while (true) {
                console.print("owner.height: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        ownerHeight = Float.parseFloat(line);
                        if (ownerHeight <= 0) {
                            console.printError("Рост должен быть больше нуля");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                        console.printError("Неверный формат числа");
                    }
                }
            }

            EyeColor eyeColor = null;
            while (true) {
                console.print("owner.eyeColor (" + EyeColor.names() + "): ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        eyeColor = EyeColor.valueOf(line.toUpperCase());
                        break;
                    } catch (IllegalArgumentException e) {
                        console.printError("Неверный формат цвета глаз");
                    }
                }
            }

            HairColor hairColor = null;
            while (true) {
                console.print("owner.hairColor (" + HairColor.names() + "): ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        hairColor = HairColor.valueOf(line.toUpperCase());
                        break;
                    } catch (IllegalArgumentException e) {
                        console.printError("Неверный формат цвета волос");
                    }
                }
            }

            Country nationality = null;
            while (true) {
                console.print("owner.nationality (" + Country.names() + "): ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        nationality = Country.valueOf(line.toUpperCase());
                        break;
                    } catch (IllegalArgumentException e) {
                        console.printError("Неверный формат национальности");
                    }
                }
            }

            Location ownerLocation = askLocation(console);

            return new Person(ownerName, ownerHeight, eyeColor, hairColor, nationality, ownerLocation);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static Location askLocation(Console console) throws AskBreak {
        try {
            long x;
            while (true) {
                console.print("location.x: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        x = Long.parseLong(line);
                        break;
                    } catch (NumberFormatException e) {
                        console.printError("Неверный формат числа");
                    }
                }
            }

            Long y;
            while (true) {
                console.print("location.y: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        y = Long.parseLong(line);
                        break;
                    } catch (NumberFormatException e) {
                        console.printError("Неверный формат числа");
                    }
                }
            }

            String name;
            while (true) {
                console.print("location.name: ");
                name = console.readln().trim();
                if (name.equals("exit")) throw new AskBreak();
                if (!name.isEmpty()) break;
            }

            return new Location(x, y, name);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }
}

