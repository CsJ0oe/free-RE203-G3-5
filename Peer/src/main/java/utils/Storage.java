package utils;

import file.FileInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import peer.Piece;

public class Storage {

    public static byte[] readPiece(FileInfo f, int piece) throws IOException {
        int size = Globals.pieceSize;
        int start = piece * size;
        return Base64.getEncoder().encode(readFromFile(f.getPath(), start, size));
    }

    public static void writePiece(Piece piece) throws IOException {
        File dir = new File(Globals.getTmpPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = Globals.getTmpPath() + "/" + piece.getKey() + "." + piece.getIndex();
        byte[] bytes = Base64.getDecoder().decode(piece.getData());
        writeToFile(filename, bytes, bytes.length);
    }

    public static String assemblePieces(FileInfo f) {
        String piecename = Globals.getTmpPath() + "/" + f.getKey() + ".";
        try {
            for (int i = 0; i < f.getNbPieces(); i++) {
                byte[] data = readFromFile(piecename + i, 0, Globals.pieceSize);
                writeToFile(Globals.getFilePath()+f.getFileName(), data, data.length);
            }
        } catch (IOException ex) {
            Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Globals.getFilePath()+f.getFileName();
    }

    private static byte[] readFromFile(String filePath, int start, int size) throws FileNotFoundException, IOException {
        byte[] bytes;
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            if (file.length() < start + size) {
                size = (int) (file.length() - start);
            }
            file.seek(start);
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
