package utils;

import java.util.Random;
import file.FileDatabase;
import peer.PeerServer;
import tracker.TrackerConnection;

public class Globals {
    // TODO: move to config class
    public static String trackerIP = "127.0.0.1";
    public static int TrackerPort = 18989;
    public static int serverPort = 0;
    public static String dataPath = "data/"+(new Random()).nextInt()+"/";
    public static String tmpPostfix = "/tmp/";
    public static int pieceSize = 2048;
    public static int maxPeersPerFile = 5;
    public static int maxPiecesPerRequest = 10;
    
    public static String getFilePath() {
        return dataPath+"/";
    }
    
    public static String getTmpPath() {
        return getFilePath()+tmpPostfix+"/";
    }
    /////////////////////////////
    public static javax.swing.JTextArea logArea;
    public static FileDatabase fileDatabase;
    public static TrackerConnection tracker;
    public static PeerServer peerServer;
}
