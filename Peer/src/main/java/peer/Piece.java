package peer;

public class Piece {

    private final String key;
    private final int index;
    private final byte[] data;

    public Piece(String key, int id, byte[] data) {
        this.key = key;
        this.index = id;
        this.data = data;
    }

    public String getKey() {
        return key;
    }
    
    public int getIndex() {
        return index;
    }

    public byte[] getData() {
        return data;
    }

}
