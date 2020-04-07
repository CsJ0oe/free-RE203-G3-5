import java.net.InetAddress;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("USAGE : Client TRACKER_IP TRACKER_PORT");
            System.exit(-1);
        }
        /*Tracker information */
        Config.trackerAddress = args[0];
        Config.trackerPort = Integer.parseInt(args[1]);

        /* Determines the IP address of a host, given the host's name or textual representation of its IP address. */
        InetAddress addr = InetAddress.getByName(Config.trackerAddress);
        /* connect to the tracker*/
        Config.trackerSocket = new Socket(addr, Config.trackerPort);

        Config.myAddress = InetAddress.getLocalHost().getHostAddress();
        Config.myPort = 2010;

        /* data folder */
        Config.dataDir = args[2];

        (new CommandHandler()).start();

        //serverSocket = new ServerSocket(port);
        //while(true){
        //    Socket socket = serverSocket.accept();
        //    peerQueue.add(socket);
        //}

    }
    
}
