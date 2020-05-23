package connection;

public class Message {
    
    private final char type;
    private final StringBuffer msg = new StringBuffer();

    public Message(char type) {
        this.type = type;
    }

    protected final void append(String s) {
        msg.append(s);
    }
    
    protected final void append(int i) {
        msg.append(Integer.valueOf(i));
    }
    
    protected final void append(Integer i) {
        msg.append(i.toString());
    }
    
    protected final void append(byte[] data) {
        for (byte b : data) {
            msg.append(b);
        }
    }
    
    public final char getType() {
        return this.type;
    }
    
    @Override
    public final String toString() {
        return msg.toString();
    }
    
}
