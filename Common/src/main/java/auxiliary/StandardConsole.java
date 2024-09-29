package auxiliary;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Для ввода команд и вывода результата
 */
public class StandardConsole implements Console {
    private static final String P1 = "$ ";
    private static Scanner fileScanner = null;
    private static Scanner defScanner = new Scanner(System.in);

    /**
     * Выводит obj.toString() в консоль
     * @param obj Объект для печати
     */
    public void print(Object obj) {
        System.out.print(obj);
    }

    /**
     * Выводит obj.toString() + \n в консоль
     * @param obj Объект для печати
     */
    public void println(Object obj) {
        System.out.println(obj);
    }

    /**
     * Выводит ошибка: obj.toString() в консоль
     * @param obj Ошибка для печати
     */
    public void printError(Object obj) {
        System.out.println("Error: " + obj);
    }

    public String readln() throws NoSuchElementException, IllegalStateException {
        return (fileScanner!=null?fileScanner:defScanner).nextLine();
    }

    public boolean isCanReadln() throws IllegalStateException {
        return (fileScanner != null ? fileScanner : defScanner).hasNextLine();
    }

    /**
     * Выводит prompt1 текущей консоли
     */
    public void prompt() {
        print(P1);
    }

    /**
     * @return prompt1 текущей консоли
     */
    public String getPrompt() {
        return P1;
    }

    public void selectFileScanner(Scanner scanner) {
        this.fileScanner = scanner;
    }

    public void selectConsoleScanner() {
        this.fileScanner = null;
    }
}
