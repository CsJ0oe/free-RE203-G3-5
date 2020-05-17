package tracker.msgs;

import utils.Message;

public class Look extends Message {

    public Look(String filename) {
        append("look [filename=\"");
        append(filename);
        append("\"]");
    }

}
