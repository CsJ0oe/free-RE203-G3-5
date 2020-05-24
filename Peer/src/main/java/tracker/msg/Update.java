package tracker.msg;

import java.util.ArrayList;
import connection.Message;
import file.FileInfo;
import utils.Logger;

public class Update extends Message {

    public Update(ArrayList<FileInfo> seeds, ArrayList<FileInfo> leechs) {
        super('u');
        append("update");
        if (!seeds.isEmpty()) {
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
            append("]");
        }
        if (!leechs.isEmpty()) {
            append(" leech [");
            boolean first = true;
            for (FileInfo leech : leechs) {
                if (first) {
                    first = false;
                } else {
                    append(" ");
                }
                append(leech.getKey());
            }
            append("]");
        }
        Logger.log("< "+this);
    }
}
