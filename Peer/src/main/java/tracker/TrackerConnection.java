package tracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import tracker.msgs.Announce;
import tracker.msgs.GetFile;
import tracker.msgs.Look;
import tracker.msgs.Update;
import file.FileInfo;
import utils.Globals;
import utils.Logger;
import peer.PeerInfo;
import utils.TcpClient;

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
                Globals.tracker.update(Globals.fileDatabase.getSeedFiles(),
                                       Globals.fileDatabase.getLeechFiles());
            } catch (IOException ex) {
                Logger.log(ex.toString());
            }
        }, 10, 10, TimeUnit.SECONDS);
        while (true) {
            try {
                this.handleResponse(con.recv());
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

    private void handleResponse(String s) {
        Logger.log("> " + s);
        String[] tok = s.replaceAll("[\\[\\]]", "").split(" ");
        if (tok[0].length() == 0) {
            Logger.log("! tracker empty resp");
            return;
        }
        switch (tok[0].charAt(0)) {
            case 'l': { // list
                Globals.fileDatabase.clearRemote();
                for (int i = 1; i < tok.length; i += 4) {
                    Globals.fileDatabase.addRemote(new FileInfo(tok[i],
                        Integer.parseInt(tok[i + 1]),
                        Integer.parseInt(tok[i + 2]),
                        tok[i + 3],
                        FileInfo.Types.REMOTE));
                }
                Globals.fileDatabase.showRemote();
            }
            break;
            case 'p': { // peers
                for (int i = 1; i < tok.length; i++) {
                    String[] tok2 = tok[i].split(":");
                    Globals.peerDatabase.add(tok[1],
                        new PeerInfo(tok2[0],
                            Integer.parseInt(tok2[1])));
                }
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
