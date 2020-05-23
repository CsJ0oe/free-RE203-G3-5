package peer;

import connection.Message;
import file.FileInfo;
import java.io.IOException;
import java.util.ArrayList;
import peer.PeerInfo;
import peer.msg.GetPieces;
import peer.msg.Interested;
import utils.Logger;
import connection.TcpClient;
import java.util.logging.Level;
import peer.msg.Data;
import peer.msg.Have;
import utils.Storage;

public class PeerConnection extends Thread {

    public enum Status {
        IDLE, CONNECTED, DOWNLOADING, UPLOADING, DISCONNECTED
    }

    private TcpClient con;
    private Status status;
    private final FileInfo file;
    private final PeerInfo peer;
    private boolean[] AvailableMap;
    private final boolean[] DownloadedMap;

    public PeerConnection(FileInfo file, PeerInfo peer) {
        this.peer = peer;
        this.file = file;
        this.AvailableMap = new boolean[file.getLength()];
        this.DownloadedMap = new boolean[file.getLength()];
        con = null;
    }

    int handleResponse(Message m) {
        switch (m.getType()) {
            case 'h': { // have $Key $BufferMap
                this.AvailableMap = ((Have) m).getBufferMap();
                return 0;
            }// break;
            case 'd': { // data $Key [$Index1:$Piece1 $Index2:$Piece2 $Index3:$Piece3 …]
                ((Data) m).getPieces().forEach((piece) -> {
                    try {
                        // save data
                        Storage.writePiece(piece);
                        //update local buffermap DownloadedMap
                        this.DownloadedMap[piece.getIndex()] = true;
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(PeerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                return 1;
            }// break;
            default: { // unknown
                return -1;
            }// break;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                switch (handleResponse(con.recvMsg())) {
                    case 0: { // start downloading
                        ArrayList<Integer> ps = file.selectPiecesToDownload(AvailableMap, DownloadedMap);
                        if (ps.isEmpty()) { // exit thread
                            this.stop();
                        }
                        con.sendMsg(new GetPieces(file.getKey(), ps));
                    }
                    break;
                    case 1: { // send intrested
                        con.sendMsg(new Interested(file.getKey()));
                    }
                    break;
                }
            } catch (IOException ex) {
                Logger.log(ex.toString());
                break;
            }
        }
    }

    public void connect() throws IOException {
        con = new TcpClient(peer.getIp(), peer.getPort());
        start();
        con.sendMsg(new Interested(file.getKey()));
    }

}
