package peer;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import file.FileInfo;
import peer.msg.Data;
import peer.msg.Have;
import utils.Globals;
import utils.Logger;
import connection.Message;
import connection.NotOk;
import connection.TcpClient;
import peer.msg.GetPieces;
import peer.msg.Interested;

public class ServerConnection extends Thread {

    TcpClient con;

    public ServerConnection(Socket soc) throws IOException {
        this.con = new TcpClient(soc);
        Logger.log("new Client connected " + con.getIP() + ":" + con.getPort());
    }

    @Override
    public void run() {
        while (true) {
            try {
                con.sendMsg(handleResponse(con.recvMsg()));
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    Message handleResponse(Message m) {
        switch (m.getType()) {
            case 'i': { // interested $Key
                FileInfo f = Globals.fileDatabase.getByHash(((Interested)m).getKey());
                if (f == null) {
                    return new NotOk();
                }
                return new Have(f);
            }// break;
            case 'g': { // getpieces $Key [$Index1 $Index2 $Index3 …]
                FileInfo f = Globals.fileDatabase.getByHash(((GetPieces)m).getKey());
                if (f == null) {
                    return new NotOk();
                }
                try {
                    return new Data(f, ((GetPieces)m).getPieces());
                } catch (IOException ex) {
                    Logger.log("error: serverConnection: data");
                }
            }// break;
            default: { // unknown
                return new NotOk();
            }// break;
        }
    }
}
