package peer.msg;

import file.FileInfo;
import java.util.BitSet;
import connection.Message;
import utils.ByteTab;
import utils.Logger;

public class Have extends Message {

    private final String key;
    private final boolean[] BufferMap;

    public Have(FileInfo f) {
        super('h');
        this.key = null;
        this.BufferMap = null;
        BitSet bm = f.getBufferMap();
        append("have ");
        append(f.getKey());
        append(" ");
        for (int i = 0; i < bm.length(); i++) {
            append(bm.get(i) == true ? "1" : "0");
        }
        Logger.log("> have "+f.getKey());
    }

    public Have(ByteTab s) { // have $Key $BufferMap
        super('h');
        this.key = s.nextWord();
        String a = s.nextWord();
        this.BufferMap = new boolean[a.length()];
        for (int i = 0; i < a.length(); i++) {
            this.BufferMap[i] = (a.charAt(i) == '1');
        }
        Logger.log("> have "+key);
    }

    public String getKey() {
        return key;
    }

    public boolean[] getBufferMap() {
        return BufferMap;
    }

}
