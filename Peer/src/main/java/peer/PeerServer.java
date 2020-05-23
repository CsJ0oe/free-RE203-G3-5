package peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import utils.Globals;
import utils.Logger;

public class PeerServer extends Thread {

    ServerSocket soc;

    public PeerServer() throws IOException {
        this.soc = new ServerSocket(Globals.serverPort);
        if (Globals.serverPort == 0) {
            Globals.serverPort = soc.getLocalPort();
            Logger.log("listening on port: " + soc.getLocalPort());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = soc.accept();
                (new ServerConnection(clientSocket)).start();
            } catch (IOException ex) {
                Logger.log(ex.toString());
            }
        }
    }
    
}
 