package peer.msg;

import connection.Message;
import utils.ByteTab;
import utils.Logger;

public class Interested extends Message {

    private final String key;
    
    public Interested(String key) {
        super('i');
        this.key = key;
        append("interested ");
        append(key);
        Logger.log("< interested "+key);
    }

    public Interested(ByteTab s) { // interested $Key
        super('i');
        this.key = s.nextWord();
        Logger.log("> interested "+key);
    }
    
    public String getKey() {
        return key;
    }
    
}
