package peer.msg;

import file.FileInfo;
import java.util.BitSet;
import connection.Message;

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
    }

    public Have(String[] tok) { // have $Key $BufferMap
        super('h');
        this.key = tok[1].strip();
        this.BufferMap = new boolean[tok[2].length()];
        for (int i = 0; i < tok[2].length(); i++) {
            this.BufferMap[i] = (tok[2].charAt(i) == '1');
        }
    }

    public String getKey() {
        return key;
    }

    public boolean[] getBufferMap() {
        return BufferMap;
    }

}
