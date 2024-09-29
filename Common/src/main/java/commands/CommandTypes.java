package commands;

import java.io.Serializable;

public enum CommandTypes implements Serializable {

    ADD("add"),
    ADDIFMIN("add_if_min"),
    CLEAR("clear"),
    FILTERBYOWNER("filter_by_owner"),
    HISTORY("history"),
    INFO("info"),
    MINBYPARTNUMBER("min_by_part_number"),
    PRINTFIELDASCENDINGUNITOFMEASURE("print_field_ascending_unit_of_measure"),
    REMOVEBYID("remove_by_id"),
    REMOVELOWER("remove_lower"),
    SAVE("save"),
    SHOW("show"),
    UPDATE("update"),
    EXIT("exit"),
    HELP("help"),
    REGISTER("register"),
    LOGIN("login"),
    EXECUTESCRIPT("execute_script");
    private String type;

    private CommandTypes(String type) {
        this.type = type;
    }

    public String Type() {
        return type;
    }

    private static final long serialVersionUID = 14L;

    public static CommandTypes getByString(String string) {
        try {

            return CommandTypes.valueOf(string.replace(Character.toString('_'), "").toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }
    }
}
