package utils;

import file.FileDatabase;
import peer.PeerDatabase;
import peer.server.PeerServer;
import tracker.TrackerConnection;

public class Globals {
    // TODO: move to config class
    public static String trackerIP = "127.0.0.1";
    public static int TrackerPort = 18989;
    public static int serverPort = 0;
    public static String dataPath = "data/";
    public static int pieceSize = 1024;
    public static int maxPeersPerFile = 5;
    public static javax.swing.JTextArea logArea;
    
    public static String getFilePath() {
        return dataPath;//+serverPort+"/";
    }
    /////////////////////////////
    public static FileDatabase fileDatabase;
    public static PeerDatabase peerDatabase = new PeerDatabase();
    public static TrackerConnection tracker;
    public static PeerServer peerServer;
}
