package file;

import peer.PeerInfo;
import java.util.ArrayList;

public class FileManager extends Thread {

    private String key;
    private ArrayList<PeerInfo> peerList;
    
    public FileManager(String key) {
        this.key = key;
        peerList = new ArrayList<>();
    }

    @Override
    public void run() {
        
    }
    
}
