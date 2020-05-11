package utils;

public class FileInfo {

    public enum Types {SEED, LEECH};
    
    private final String name;
    private final int length;
    private final int pieceSize;
    private final String key;
    private final Types type;

    public FileInfo(String name, int length, int pieceSize, String key, Types type) {
        this.name = name;
        this.length = length;
        this.pieceSize = pieceSize;
        this.key = key;
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " " + length + " " + pieceSize + " " + key;
    }
    
    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getPieceSize() {
        return pieceSize;
    }

    public String getKey() {
        return key;
    }
    
    public Types getType() {
        return type;
    }
}
