package utils;

import file.FileInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import peer.Piece;

public class Storage {

    public static byte[] readPiece(FileInfo f, int piece) throws IOException {
        int size = Globals.pieceSize;
        int start = piece * size;
        return readFromFile(f.getPath(), start, size);
    }

    public static void writePiece(Piece piece) throws IOException {
        File dir = new File(Globals.tmpPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = Globals.tmpPath + "/" + piece.getKey() + "." + piece.getIndex();
        byte[] bytes = piece.getData().getBytes();
        writeToFile(filename, bytes, bytes.length);
    }

    private static byte[] readFromFile(String filePath, int start, int size) throws FileNotFoundException, IOException {
        byte[] bytes;
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            if (file.length() < start + size) {
                size = (int) (file.length() - start);
            }   file.seek(start);
            bytes = new byte[size];
            file.read(bytes);
        }
        return bytes;
    }

    private static void writeToFile(String filePath, byte[] bytes, int size) throws FileNotFoundException, IOException {
        try (FileOutputStream out = new FileOutputStream(filePath, true)) {
            out.write(bytes, 0, size);
        }
    }
}
