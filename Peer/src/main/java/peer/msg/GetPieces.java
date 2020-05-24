package peer.msg;

import java.util.ArrayList;
import connection.Message;
import utils.ByteTab;
import utils.Logger;

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
        String tmp = "";
        for (Integer piece : pieces) {
            if (first) {
                first = false;
            } else {
                append(" ");
            }
            append(piece.toString());
            tmp+=piece+" ";
        }
        append("]");
        Logger.log("< "+this);
    }

    public GetPieces(ByteTab t) { // getpieces $Key [$Index1 $Index2 $Index3 …]
        super('g');
        this.key = t.nextWord();
        String tmp = "";
        for (int i = 2; i < t.length(); i++) {
            int x = t.nextInt();
            list.add(x);
            tmp+=x+" ";
        }
        Logger.log("> getpieces "+key+" ["+tmp+"]");
        
    }
    
    public String getKey() {
        return key;
    }
    
    public ArrayList<Integer> getPieces() {
        return list;
    }
    
}
