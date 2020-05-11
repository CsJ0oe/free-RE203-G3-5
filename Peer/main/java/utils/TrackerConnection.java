
package utils;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTextArea;

public class TrackerConnection {
    
    private final TcpClient con;
    private final recvThread th;
    
    public TrackerConnection (String ip, int port, JTextArea txt) throws IOException {
        con = new TcpClient(ip, port);
        th = new recvThread(con, txt);
        th.start();
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
