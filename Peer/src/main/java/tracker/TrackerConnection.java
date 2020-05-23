package tracker;

import tracker.msg.FileList;
import connection.Message;
import tracker.msg.Peers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import tracker.msg.Announce;
import tracker.msg.GetFile;
import tracker.msg.Look;
import tracker.msg.Update;
import file.FileInfo;
import utils.Globals;
import utils.Logger;
import peer.PeerInfo;
import connection.TcpClient;
import file.FileInfoLight;

public final class TrackerConnection extends Thread {

    private final TcpClient con;

    public TrackerConnection() throws IOException {
        con = new TcpClient(Globals.trackerIP,
                            Globals.TrackerPort);
        this.announce(Globals.serverPort,
                      Globals.fileDatabase.getSeedFiles(),
                      Globals.fileDatabase.getLeechFiles());
    }

    @Override
    public void run() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                Globals.tracker.announce(Globals.serverPort,Globals.fileDatabase.getSeedFiles(),
                                       Globals.fileDatabase.getLeechFiles());
            } catch (IOException ex) {
                Logger.log(ex.toString());
            }
        }, 10, 10, TimeUnit.SECONDS);
        while (true) {
            try {
                this.handleResponse(con.recvMsg());
            } catch (IOException ex) {
                Logger.log(ex.toString());
                break;
            }
        }
    }

    public void announce(int port, ArrayList<FileInfo> seeds, ArrayList<FileInfo> leechs) throws IOException {
        con.sendMsg(new Announce(port, seeds, leechs));
    }

    public void update(ArrayList<FileInfo> seeds, ArrayList<FileInfo> leechs) throws IOException {
        con.sendMsg(new Update(seeds, leechs));
    }

    public void look(String filename) throws IOException {
        con.sendMsg(new Look(filename));

    }

    public void getfile(String filekey) throws IOException {
        con.sendMsg(new GetFile(filekey));
    }

    public void close() throws IOException {
        con.close();
    }

    private void handleResponse(Message m) {
        switch (m.getType()) {
            case 'l': { // list
                Globals.fileDatabase.clearRemote();
                ((FileList)m).getFiles().forEach((file) -> {
                    Globals.fileDatabase.addRemote(new FileInfo(file.getName(),
                        file.getLength(),
                        file.getPieceSize(),
                        file.getKey(),
                        FileInfo.Types.REMOTE,
                        null));
                });
                Globals.fileDatabase.showRemote();
            }
            break;
            case 'p': { // peers
                ((Peers)m).getPeers().forEach((peer) -> {
                    Globals.fileDatabase.getByHash(((Peers)m).getKey()).addPeer(
                        new PeerInfo(peer.getIp(), peer.getPort()));
                });
            }
            break;
            case 'o': { // ok
            }
            break;
            case 'n': { // nok
            }
            break;
            default: { // unknown
            }
            break;
        }

    }
}
