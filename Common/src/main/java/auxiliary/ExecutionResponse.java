package auxiliary;

import java.io.Serializable;

public class ExecutionResponse implements Serializable {
    private boolean exitCode;
    private String massage;
    /**
     * Конструктор класса ExecutionResponse с указанием кода завершения и сообщения.
     *
     * @param code Код завершения операции (true - успешно, false - ошибка).
     * @param s    Сообщение об операции.
     */
    public ExecutionResponse(boolean code, String s) {
        exitCode = code;
        massage = s;
    }
    /**
     * Конструктор класса ExecutionResponse для успешной операции с указанием сообщения.
     *
     * @param s Сообщение об успешной операции.
     */
    public ExecutionResponse(String s) {
        this(true, s);
    }
    /**
     * Метод для получения кода завершения операции.
     *
     * @return Код завершения операции (true - успешно, false - ошибка).
     */
    public boolean getExitCode() { return exitCode; }
    /**
     * Метод для получения сообщения об операции.
     *
     * @return Сообщение об операции.
     */
    public String getMassage() { return massage; }
    /**
     * Переопределение метода toString для получения строкового представления объекта ExecutionResponse.
     *
     * @return Строковое представление объекта ExecutionResponse в формате "exitCode;message".
     */
    public String toString() { return String.valueOf(exitCode)+";"+massage; }
}
