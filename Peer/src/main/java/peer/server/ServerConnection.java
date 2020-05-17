package peer.server;

import java.io.IOException;
import java.net.Socket;
import java.util.BitSet;
import java.util.logging.Level;
import file.FileInfo;
import utils.Globals;
import utils.Logger;
import utils.TcpClient;

public class ServerConnection extends Thread {
    
    TcpClient con;

    public ServerConnection(Socket soc) throws IOException {
        this.con = new TcpClient(soc);
    }

    @Override
    public void run() {
        while(true) {
            try {
                con.sendAndFlush(handleResponse(con.recv()));
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    String handleResponse(String s) {
        Logger.log("> " + s );
        String[] tok = s.replaceAll("[\\[\\]]", "").split(" ");
        if (tok[0].length() == 0) {
            Logger.log("! peer empty cmd\n");
            return null;
        }
        switch (tok[0].charAt(0)) {
            case 'i': { // interested $Key -> have $Key $BufferMap
                FileInfo f = Globals.fileDatabase.getByHash(tok[1]);
                if (f == null) return "nok";
                BitSet bm = f.getBufferMap();
                StringBuilder ss = new StringBuilder();
                for( int i = 0; i < bm.length();  i++ )
                {
                    ss.append( bm.get( i ) == true ? 1: 0 );
                }
                Logger.log("< " + ss.toString() );
                return ss.toString();
            }// break;
            case 'g': { // getpieces $Key [$Index1 $Index2 $Index3 …] -> data $Key [$Index1:$Piece1 $Index2:$Piece2 $Index3:$Piece3 …]
                return "not yet";
            }// break;
            default: { // unknown
                return "nok";
            }// break;
        }
    }
}
