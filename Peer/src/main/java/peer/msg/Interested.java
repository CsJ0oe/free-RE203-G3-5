package peer.msg;

import connection.Message;

public class Interested extends Message {

    private final String key;
    
    public Interested(String key) {
        super('i');
        this.key = key;
        append("interested ");
        append(key);
    }

    public Interested(String[] tok) { // interested $Key
        super('i');
        this.key = tok[1].strip();
    }
    
    public String getKey() {
        return key;
    }
    
}
