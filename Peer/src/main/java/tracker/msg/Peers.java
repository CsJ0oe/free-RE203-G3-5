package tracker.msg;

import connection.Message;
import java.util.ArrayList;
import peer.PeerInfo;
import utils.ByteTab;
import utils.Logger;

public class Peers extends Message {
    
    private final String key;
    private final ArrayList<PeerInfo> list = new ArrayList<>();

    public Peers(ByteTab s) { // peers $Key [$IP1:$Port1 $IP2:$Port2 …]
        super('p');
        key = s.nextWord();
        for (int i = 2; i < s.length(); i++) {
            String[] tok2 = s.nextWord().split(":");
            list.add(new PeerInfo(tok2[0].strip(),
                                  Integer.parseInt(tok2[1].strip())));
        }
        Logger.log("< "+this);
    }
    
    public String getKey() {
        return key;
    }
    
    public ArrayList<PeerInfo> getPeers() {
        return list;
    }
    
}
