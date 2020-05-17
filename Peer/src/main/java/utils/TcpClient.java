package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import tracker.msgs.Announce;

public class TcpClient {

    //private final String ip;
    //private final int port;
    //private final InetAddress address;
    private final Socket soc;
    private final BufferedOutputStream bos;
    private final BufferedInputStream bis;

    public TcpClient(String ip, int port) throws UnknownHostException, IOException {
        //this.ip = ip;
        //this.port = port;
        InetAddress address = InetAddress.getByName(ip);
        this.soc = new Socket(address, port);
        bos = new BufferedOutputStream(soc.getOutputStream());
        bis = new BufferedInputStream(soc.getInputStream());
        //TODO: use StringBuffer instead
    }

    public TcpClient(Socket soc) throws UnknownHostException, IOException {
        this.soc = soc;
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

    public void sendAndFlush(String s) throws IOException {
        send(s);
        flush();
    }

    public void sendMsg(Message msg) throws IOException {
        sendAndFlush(msg.toString()+"\n");
    }

    public String recv() throws IOException {
        String res = "";
        int stat;
        while ((stat = bis.read()) != '\n') {
            //if (stat == -1) throw new ConnectException();
            res += (char) stat;
        }
        return res;
    }

    public void close() throws IOException {
        bis.close();
        bos.close();
        soc.close();
    }

}
