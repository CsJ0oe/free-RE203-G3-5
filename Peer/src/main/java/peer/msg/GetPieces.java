package peer.msg;

import java.util.ArrayList;
import connection.Message;

public class GetPieces extends Message {

    private final ArrayList<Integer> list = new ArrayList<>();;
    private final String key;

    public GetPieces(String key, ArrayList<Integer> pieces) {
        super('g');
        this.key = null;
        append("getpieces ");
        append(key);
        append(" [");
        boolean first = true;
        for (Integer piece : pieces) {
            if (first) {
                first = false;
            } else {
                append(" ");
            }
            append(piece.toString());
        }
        append("]");
    }

    public GetPieces(String[] tok) { // getpieces $Key [$Index1 $Index2 $Index3 …]
        super('g');
        this.key = tok[1].strip();
        for (int i = 2; i < tok.length; i++) {
            list.add(Integer.valueOf(tok[i].strip()));
        }
        
    }
    
    public String getKey() {
        return key;
    }
    
    public ArrayList<Integer> getPieces() {
        return list;
    }
    
}
