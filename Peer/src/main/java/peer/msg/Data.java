package peer.msg;

import peer.Piece;
import file.FileInfo;
import java.util.ArrayList;
import connection.Message;
import java.io.FileNotFoundException;
import java.io.IOException;
import utils.Logger;
import utils.Storage;

public class Data extends Message {

    private final String key;
    private final ArrayList<Piece> list = new ArrayList<>();;

    public Data(FileInfo f, ArrayList<Integer> idx) throws FileNotFoundException, IOException {
        super('d');
        this.key = null;
        append("data ");
        append(f.getKey());
        append(" [");
        for (Integer id : idx) {
            append(id);
            append(":");
            append(Storage.readPiece(f, id));
        }
        append("]");
    }

    public Data(String[] tok) { //data $Key [$Index1:$Piece1 $Index2:$Piece2 $Index3:$Piece3 …]
        super('d');
        this.key = tok[1].strip();
        for (int i = 2; i < tok.length; i++) {
            String[] tok2 = tok[i].strip().split(":");
            list.add(new Piece(this.key, Integer.parseInt(tok2[0].strip()), tok2[1].strip()));
            Logger.log("data "+ tok2[0].strip());
        }
        
    }

    public String getKey() {
        return key;
    }

    public ArrayList<Piece> getPieces() {
        return list;
    }

}
