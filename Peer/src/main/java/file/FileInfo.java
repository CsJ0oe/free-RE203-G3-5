package file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.logging.Level;
import peer.PeerInfo;
import peer.PeerConnection;
import utils.Globals;
import utils.Logger;
import utils.Storage;

public class FileInfo extends Thread {



    public enum Types {
        SEED, LEECH, REMOTE, DONE
    };

    private final String name;
    private final int length;
    private final int pieceSize;
    private final int nbPieces;
    private final String key;
    private final String path;
    private final BitSet BufferMap;
    private final BitSet DownloadingBufferMap;
    private final BitSet DownloadedBufferMap;
    private Types type;
    private final ArrayList<PeerInfo> peerList;
    private final ArrayList<PeerConnection> connectedPeers;
    private int connections = 0;

    public FileInfo(String name, long length, int pieceSize, String key, Types type, String path) {
        this.name = name;
        this.length = (int)length;
        this.pieceSize = pieceSize;
        this.key = key;
        this.nbPieces = (int) Math.ceil((float) length / (float) pieceSize);
        this.type = type;
        this.path = path;
        this.peerList = new ArrayList<>();
        this.connectedPeers = new ArrayList<>();
        this.BufferMap = new BitSet(nbPieces);
        if (type == Types.SEED) {
            this.BufferMap.set(0, nbPieces);
        }
        this.DownloadingBufferMap = new BitSet(nbPieces);
        this.DownloadedBufferMap = new BitSet(nbPieces);
        if (type != Types.REMOTE) {
            start();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(3000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(FileInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (type == Types.LEECH) {
                while ((connections < Globals.maxPeersPerFile)
                    && (!peerList.isEmpty())) {
                    try {
                        PeerConnection pc = new PeerConnection(this, peerList.remove(0));
                        connectedPeers.add(pc);
                        connections++;
                        pc.connect();
                        Logger.log("connecting");
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(FileInfo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return name + " " + length + " " + pieceSize + " " + key;
    }

    public String getFileName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getPieceSize() {
        return pieceSize;
    }

    public String getKey() {
        return key;
    }

    public Types getType() {
        return type;
    }

    public String getPath() {
        return path;
    }
    
    public BitSet getBufferMap() {
        return this.BufferMap;
    }

    public void setType(Types ty) {
        if ((this.type == Types.REMOTE) && (ty != Types.REMOTE)) {
            start();
        }
        this.type = ty;
    }
    
    public int getProgress() {
        switch (type) {
            case SEED:  return 100;
            case DONE:  return 99;
            case LEECH: return 100*DownloadedBufferMap.cardinality()/nbPieces;
            default:    return -1;
        }
    }

    public void addPeer(PeerInfo peer) {
        peerList.add(peer);
    }
    
    public synchronized ArrayList<Integer> selectPiecesToDownload(boolean[] am, boolean[] dm)  {
        for (int i = 0; i < dm.length; i++) {
           DownloadedBufferMap.set(i, dm[i]);
        }
        ArrayList<Integer> res = new ArrayList<>();
        int i = 0;
        while (i < am.length && res.size() < Globals.maxPiecesPerRequest) {
            if ( am[i] == true && !BufferMap.get(i) &&
                                 !DownloadedBufferMap.get(i) &&
                                 !DownloadingBufferMap.get(i)) {
                res.add(i);
            }
            i++;
        }
        if (res.isEmpty() && DownloadedBufferMap.cardinality() == this.nbPieces) {
            // is file completed downloading
            this.type = Types.DONE;
            Storage.assemblePieces(this);
            this.type = Types.SEED;
        }
        return res;
    }

    public int getNbPieces() {
        return nbPieces;
    }
    
    
    
}
