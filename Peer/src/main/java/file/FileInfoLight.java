package file;

public class FileInfoLight {
 
    private final String name;
    private final int length;
    private final int pieceSize;
    private final String key;
    
    public FileInfoLight(String name, long length, int pieceSize, String key) {
        this.name = name;
        this.length = (int)length;
        this.pieceSize = pieceSize;
        this.key = key;
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
    
    
}
