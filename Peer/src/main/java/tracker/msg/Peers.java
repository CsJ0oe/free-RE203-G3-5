package tracker.msg;

import connection.Message;
import file.FileInfoLight;
import java.util.ArrayList;
import peer.PeerInfo;

public class Peers extends Message {
    
    private final String key;
    private final ArrayList<PeerInfo> list = new ArrayList<>();

    public Peers(String[] tok) { // peers $Key [$IP1:$Port1 $IP2:$Port2 …]
        super('p');
        key = tok[1];
        for (int i = 2; i < tok.length; i++) {
            String[] tok2 = tok[i].strip().split(":");
            list.add(new PeerInfo(tok2[0].strip(),
                                  Integer.parseInt(tok2[1].strip())));
        }
    }
    
    public String getKey() {
        return key;
    }
    
    public ArrayList<PeerInfo> getPeers() {
        return list;
    }
    
}
