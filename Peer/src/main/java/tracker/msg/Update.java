package tracker.msg;

import java.util.ArrayList;
import connection.Message;
import file.FileInfo;

public class Update extends Message {

    public Update(ArrayList<FileInfo> seeds, ArrayList<FileInfo> leechs) {
        super('u');
        append("update seed [");
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
