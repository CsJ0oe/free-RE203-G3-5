package utils;

public class Globals {
    // TODO: move to config class
    public static String filePath = "data/";
    public static String trackerIP = "127.0.0.1";
    public static int TrackerPort = 18989;
    public static int serverPort = 22222;
    /////////////////////////////
    public static TrackerConnection tracker;
    public static final FileManager fileDatabase = new FileManager();
    public static final PeerManager peerDatabase = new PeerManager();
}
