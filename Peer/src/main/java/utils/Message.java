package utils;

public class Message {
    
    private final StringBuffer msg = new StringBuffer();

    protected final void append(String s) {
        msg.append(s);
    }
    
    @Override
    public final String toString() {
        return msg.toString();
    }
}
