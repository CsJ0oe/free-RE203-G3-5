package tracker.msgs;

import java.util.ArrayList;
import utils.Message;
import file.FileInfo;

public class Announce extends Message {

    public Announce(int port, ArrayList<FileInfo> seeds, ArrayList<FileInfo> leechs) {
        append("announce listen ");
        append(String.valueOf(port));
        append(" seed [");
        boolean first = true;
        for (FileInfo seed : seeds) {
            if (first) {
                first = false;
            } else {
                append(" ");
            }
            append(seed.toString());
        }
        append("] leech [");
        first = true;
        for (FileInfo leech : leechs) {
            if (first) {
                first = false;
            } else {
                append(" ");
            }
            append(leech.toString());
        }
        append("]");
    }
}
