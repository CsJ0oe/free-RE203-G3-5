package peer;

public class Piece {

    private final String key;
    private final int index;
    private final String data;

    public Piece(String key, int id, String data) {
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

    public String getData() {
        return data;
    }

}
