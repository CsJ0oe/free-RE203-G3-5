package peer;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PeerDatabase {
    private final LinkedHashMap<String, ArrayList<PeerInfo> > peers = new LinkedHashMap<>();

    public void add(String key, PeerInfo peer) {
        ArrayList<PeerInfo> l = peers.get(key);
        if (l == null) {
            l = new ArrayList<>();
            peers.put(key, l);
        }
        l.add(peer);
    }
    
    public ArrayList<PeerInfo> get(String key) {
        return peers.get(key);
    }
    
    public PeerInfo get(String key, int index) {
        ArrayList<PeerInfo> l = peers.get(key);
        if (l != null) {
            return l.get(index);
        } else {
            return null;
        }
    }
    
}
