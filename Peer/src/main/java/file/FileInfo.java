package file;

import java.util.BitSet;

public class FileInfo {

    public enum Types {SEED, LEECH, REMOTE};
    
    private final String name;
    private final long length;
    private final int pieceSize;
    private final String key;
    private Types type;
    private BitSet BufferMap;

    public FileInfo(String name, long length, int pieceSize, String key, Types type) {
        this.name = name;
        this.length = length;
        this.pieceSize = pieceSize;
        this.key = key;
        this.type = type;
        this.BufferMap = new BitSet((int)Math.ceil((float)length/(float)pieceSize));
    }

    @Override
    public String toString() {
        return name + " " + length + " " + pieceSize + " " + key;
    }
    
    public String getName() {
        return name;
    }

    public long getLength() {
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
    
    public BitSet getBufferMap() {
        return this.BufferMap;
    }
    
    public void setType(Types ty) {
        this.type = ty;
    }
}
