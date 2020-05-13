package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient {
    
    private final String ip;
    private final int port;
    private final InetAddress address;
    private final Socket soc;
    BufferedOutputStream bos;
    BufferedInputStream bis;
    
    public TcpClient(String ip, int port) throws UnknownHostException, IOException {
        this.ip = ip;
        this.port = port;
        address = InetAddress.getByName(ip);
        soc = new Socket(address, port);
        soc.getOutputStream();
        bos = new BufferedOutputStream(soc.getOutputStream());
        bis = new BufferedInputStream(soc.getInputStream());
        //TODO: use StringBuffer instead
    }

    public void send(String msg) throws IOException {
        bos.write(msg.getBytes()); // TODO: fix charset
    }
    
    public void flush() throws IOException {
        bos.flush();
    }

    public String recv() throws IOException {
        String res = "";
        int stat;
        while((stat = bis.read()) != '\n'){
            //if (stat == -1) throw new ConnectException();
            res += (char)stat;
        }
        return res;
    }
    
    public void close() throws IOException {
        bis.close();
        bos.close();
        soc.close();
    }
}
