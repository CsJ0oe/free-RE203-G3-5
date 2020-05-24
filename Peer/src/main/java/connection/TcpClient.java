package connection;

import tracker.msg.Peers;
import tracker.msg.FileList;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import peer.msg.Data;
import peer.msg.GetPieces;
import peer.msg.Have;
import peer.msg.Interested;
import utils.ByteTab;
import utils.Logger;

public class TcpClient {

    private final String ip;
    private final int port;
    private final InetAddress address;
    private final Socket soc;
    private final BufferedOutputStream bos;
    private final BufferedInputStream bis;

    public TcpClient(String ip, int port) throws UnknownHostException, IOException {
        this.ip = ip;
        this.port = port;
        this.address = InetAddress.getByName(ip);
        this.soc = new Socket(address, port);
        bos = new BufferedOutputStream(soc.getOutputStream());
        bis = new BufferedInputStream(soc.getInputStream());
    }

    public TcpClient(Socket soc) throws UnknownHostException, IOException {
        this.soc = soc;
        this.port = soc.getPort();
        this.address = soc.getInetAddress();
        this.ip = this.address.getHostAddress();
        bos = new BufferedOutputStream(soc.getOutputStream());
        bis = new BufferedInputStream(soc.getInputStream());
    }

    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void sendMsg(Message msg) throws IOException {
        //Logger.log("< " + msg.toString());
        send(msg.toBytes());
        send("\n");
        flush();
    }

    public Message recvMsg() throws IOException {
        ByteTab s = new ByteTab(recv());
        //Logger.log("> " + new String(s.data));
        if (s.length() == 0) {
            Logger.log("! empty resp");
            return recvMsg();
        }
        
        Message res = null;
        switch (s.nextWord().charAt(0)) {
            case 'l': { //list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2 …]
                res = new FileList(s);
            }
            break;
            case 'p': { // peers $Key [$IP1:$Port1 $IP2:$Port2 …]
                res = new Peers(s);
            }
            break;
            case 'i': { // interested $Key
                res = new Interested(s);
            }
            break;
            case 'g': { // getpieces $Key [$Index1 $Index2 $Index3 …]
                res = new GetPieces(s);
            }
            break;
            case 'h': { // have $Key $BufferMap
                res = new Have(s);
            }
            break;
            case 'd': { // data $Key [$Index1:$Piece1 $Index2:$Piece2 $Index3:$Piece3 …]
                res = new Data(s);
            }
            break;
            case 'o': { // ok
                res = new Ok();
            }
            break;
            case 'n': { // nok
                res = new NotOk();
            }
            break;
            default: { // unknown
                res = recvMsg();
            }
        }
        return res;
    }

    private byte[] recv() throws IOException {
        ArrayList<Byte> tmp = new ArrayList<>();
        byte stat;
        while ((stat = (byte) bis.read()) != '\n') {
            //if (stat == -1) throw new ConnectException();
            tmp.add(stat);
        }
        byte[] res = new byte[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            res[i] = tmp.get(i);
        }
        return res;
    }

    public void close() throws IOException {
        bis.close();
        bos.close();
        soc.close();
    }

    private void send(String msg) throws IOException {
        bos.write(msg.getBytes()); // TODO: fix charset
    }
    
    private void send(byte[] msg) throws IOException {
        bos.write(msg);
    }

    private void flush() throws IOException {
        bos.flush();
    }

}
