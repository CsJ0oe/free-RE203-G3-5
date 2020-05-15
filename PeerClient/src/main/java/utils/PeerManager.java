package utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PeerManager {
    private final LinkedHashMap<String, ArrayList<PeerInfo> > peers = new LinkedHashMap<>();

    void add(String key, PeerInfo peer) {
        ArrayList<PeerInfo> l = peers.get(key);
        if (l == null) {
            l = new ArrayList<>();
            peers.put(key, l);
        }
        l.add(peer);
    }
    
    ArrayList<PeerInfo> get(String key) {
        return peers.get(key);
    }
    
    PeerInfo get(String key, int index) {
        ArrayList<PeerInfo> l = peers.get(key);
        if (l != null) {
            return l.get(index);
        } else {
            return null;
        }
    }
    
}
