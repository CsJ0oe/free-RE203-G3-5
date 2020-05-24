package connection;

import java.util.ArrayList;

public class Message {

    private final char type;
    private final ArrayList<Byte> msg = new ArrayList<>();

    public Message(char type) {
        this.type = type;
    }

    protected final void append(String s) {
        append(s.getBytes());
    }

    protected final void append(int i) {
        append(Integer.valueOf(i));
    }

    protected final void append(Integer i) {
        append(i.toString());
    }

    protected final void append(byte[] data) {
        for (byte b : data) {
            msg.add(b);
        }
    }

    public final char getType() {
        return this.type;
    }

    public byte[] toBytes() {
        byte[] res = new byte[msg.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = msg.get(i);
        }
        return res;
    }

    @Override
    public final String toString() {
        return new String(toBytes());
    }

}
