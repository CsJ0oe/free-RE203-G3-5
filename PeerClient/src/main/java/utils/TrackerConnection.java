
package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;



public final class TrackerConnection {
    
    private final TcpClient con;
    private final recvThread rth;
    private final sendThread sth;
    private final JTextArea txt;
    
    public TrackerConnection (JTextArea txt) throws IOException {
        this.txt = txt;
        con = new TcpClient(Globals.trackerIP,
                            Globals.TrackerPort);
        rth = new recvThread(con, txt);
        rth.start();
        announce(Globals.serverPort,
                 Globals.fileDatabase.getSeedFiles(),
                 Globals.fileDatabase.getLeechFiles());
        sth = new sendThread(con, txt);
        sth.start();
    }
    
    public void announce(int port, ArrayList<FileInfo> seeds, ArrayList<FileInfo> leechs) throws IOException {
        String msg = "announce listen "+ String.valueOf(port) +" seed [";
        boolean first = true;
        for (FileInfo seed : seeds) {
            if (first) {
                first = false;
            } else {
                msg += " ";
            }
            msg += seed;
        }
        msg += "] leech [";
        first = true;
        for (FileInfo leech : leechs) {
            if (first) {
                first = false;
            } else {
                msg += " ";
            }
            msg += leech;
        }
        msg += "]\n";
        con.send(msg);
        con.flush();
    }
    
    public void update(ArrayList<FileInfo> seeds, ArrayList<FileInfo> leechs) throws IOException {
        String msg = "update seed [";
        boolean first = true;
        for (FileInfo seed : seeds) {
            if (first) {
                first = false;
            } else {
                msg += " ";
            }
            msg += seed;
        }
        msg += "] leech [";
        first = true;
        for (FileInfo leech : leechs) {
            if (first) {
                first = false;
            } else {
                msg += " ";
            }
            msg += leech;
        }
        msg += "]\n";
        txt.append("<"+msg+"\n");
        con.send(msg);
        con.flush();
    }
    
    public void look(String filename) throws IOException {
        con.send("look [filename=\""+filename+"\"]\n");
        con.flush();
    }
    
    public void getfile(String filekey) throws IOException {
        con.send("getfile "+filekey+"\n");
        con.flush();
    }
    
    public void close() throws IOException {
        con.close();
    }
}

class recvThread extends Thread {
    
    TcpClient con;
    JTextArea txt;
    
    public recvThread(TcpClient con, JTextArea txt) {
        this.con = con;
        this.txt = txt;
    }
    
    @Override
    public void run() {
        String s;
        while (true) {
            try {
                s = con.recv();
                txt.append(">"+s+"\n");
            } catch (IOException ex) {
                Logger.getLogger(recvThread.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }
    
}

class sendThread extends Thread {
    
    TcpClient con;
    JTextArea txt;
    
    public sendThread(TcpClient con, JTextArea txt) {
        this.con = con;
        this.txt = txt;
    }
    
    @Override
    public void run() {
        String s;
        while (true) {
            try {
                sleep(10000);
                Globals.tracker.update(Globals.fileDatabase.getSeedFiles(),
                                       Globals.fileDatabase.getLeechFiles());
            } catch (IOException ex) {
                Logger.getLogger(recvThread.class.getName()).log(Level.SEVERE, null, ex);
                break;
            } catch (InterruptedException ex) {
                Logger.getLogger(sendThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}