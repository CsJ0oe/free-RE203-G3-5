package tracker.msg;

import connection.Message;

public class Look extends Message {

    public Look(String filename) {
        super('l');
        append("look [filename=\"");
        append(filename);
        append("\"]");
    }

}
