package utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

public class recvThread extends Thread {
    
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
