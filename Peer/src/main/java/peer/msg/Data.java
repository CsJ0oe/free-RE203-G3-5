package peer.msg;

import peer.Piece;
import file.FileInfo;
import java.util.ArrayList;
import connection.Message;
import java.io.FileNotFoundException;
import java.io.IOException;
import utils.ByteTab;
import utils.Logger;
import utils.Storage;

public class Data extends Message {

    private final String key;
    private final ArrayList<Piece> list = new ArrayList<>();

    ;

    public Data(FileInfo f, ArrayList<Integer> idx) throws FileNotFoundException, IOException {
        super('d');
        this.key = null;
        append("data ");
        append(f.getKey());
        append(" [");
        String tmp = ""; // for log
        boolean first = true;
        for (Integer id : idx) {
            if (first) {
                first = false;
            } else {
                append(" ");
            }
            append(id);
            append(":");
            append(Storage.readPiece(f, id));
            tmp += id + " ";
        }
        append("]");
        Logger.log("< data "+key+" ["+tmp+"]");
        //Logger.log("< " + this);
    }

    public Data(ByteTab t) { //data $Key [$Index1:$Piece1 $Index2:$Piece2 $Index3:$Piece3 …]
        super('d');
        this.key = t.nextWord();
        String tmp = "";
        for (int i = 2; i < t.length(); i++) {
            int a = t.nextInt(':');
            byte[] b = t.nextBytes();
            list.add(new Piece(this.key, a, b));
            tmp += a + " ";

        }
        Logger.log("> data "+key+" ["+tmp+"]");
        //Logger.log("> " + this);
    }

    public String getKey() {
        return key;
    }

    public ArrayList<Piece> getPieces() {
        return list;
    }

}
