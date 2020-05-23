package utils;

import file.FileDatabase;
import peer.PeerDatabase;
import peer.PeerServer;
import tracker.TrackerConnection;

public class Globals {
    // TODO: move to config class
    public static String trackerIP = "127.0.0.1";
    public static int TrackerPort = 18989;
    public static int serverPort = 0;
    public static String dataPath = "data/";
    public static String tmpPath = "data/tmp/";
    public static int pieceSize = 1024;
    public static int maxPeersPerFile = 5;
    public static int maxPiecesPerRequest = 3;
    
    public static String getFilePath() {
        return dataPath;//+serverPort+"/";
    }
    /////////////////////////////
    public static javax.swing.JTextArea logArea;
    public static FileDatabase fileDatabase;
    public static TrackerConnection tracker;
    public static PeerServer peerServer;
}
