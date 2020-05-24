package file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import utils.Globals;

public final class FileDatabase extends AbstractTableModel {

    private final LinkedHashMap<String, FileInfo> localFiles = new LinkedHashMap<>();
    private final LinkedHashMap<String, FileInfo> remoteFiles = new LinkedHashMap<>();
    private LinkedHashMap<String, FileInfo> currentFiles;
    String[] columnNames = {"File",
        "Length",
        "Piece Size",
        "Hash",
        "Type",
        "Progress"};

    public FileDatabase() {
        currentFiles = localFiles;
        File directory = new File(Globals.getFilePath());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        for (final File fileEntry : directory.listFiles()) {
            if (fileEntry.isFile()) {
                try {
                    this.add(new FileInfo(fileEntry.getName(),
                        fileEntry.length(),
                        Globals.pieceSize,
                        FileDatabase.md5(fileEntry.toPath()),
                        FileInfo.Types.SEED,
                        fileEntry.getCanonicalPath()));
                } catch (IOException ex) {
                    Logger.getLogger(FileDatabase.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        Timer timer = new Timer(0, (ActionEvent e) -> {
            Globals.fileDatabase.forceRefresh();
        });
        timer.setDelay(5000);
        timer.start();
    }

    public void forceRefresh() {
        fireTableDataChanged();
    }
    
    private static String md5(Path path) throws IOException {
        try {
            byte[] b = Files.readAllBytes(path);
            byte[] hash = MessageDigest.getInstance("MD5").digest(b);
            String returnVal = "";
            for (int i = 0; i < hash.length; i++) {
                returnVal += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
            }
            return returnVal.toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public void importFile(String path) throws IOException {
        Path original = Paths.get(path);
        Path copie = Paths.get(Globals.getFilePath() + "/"
            + original.getFileName().toString());
        File dir = copie.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Files.copy(original, copie, StandardCopyOption.REPLACE_EXISTING);
        add(new FileInfo(original.getFileName().toString(),
            new File(path).length(),
            Globals.pieceSize,
            md5(copie),
            FileInfo.Types.SEED,
            copie.toAbsolutePath().normalize().toString()));
    }

    public void add(FileInfo file) {
        if (localFiles.get(file.getKey()) == null) {
            localFiles.put(file.getKey(), file);
            fireTableDataChanged();
        }
    }

    public void remove(FileInfo file) {
        localFiles.remove(file.getKey());
        fireTableDataChanged();
    }

    public FileInfo getByHash(String hash) {
        return localFiles.get(hash);
    }

    public void addRemote(FileInfo file) {
        remoteFiles.put(file.getKey(), file);
    }

    public FileInfo getRemote(int index) {
        return (new ArrayList<>(remoteFiles.values())).get(index);
    }

    public void clearRemote() {
        remoteFiles.clear();
    }

    public boolean isRemote() {
        return currentFiles == remoteFiles;
    }

    public String makeLocal(int i) {
        FileInfo f = (new ArrayList<>(remoteFiles.values())).get(i);
        f.setType(FileInfo.Types.LEECH);
        remoteFiles.remove(f.getKey());
        this.add(f);
        fireTableDataChanged();
        return f.getKey();
    }

    public void showRemote() {
        currentFiles = remoteFiles;
        fireTableStructureChanged();
    }

    public void hideRemote() {
        currentFiles = localFiles;
        fireTableDataChanged();
    }

    public ArrayList<FileInfo> getSeedFiles() {
        ArrayList<FileInfo> result = new ArrayList<>();
        for (FileInfo file : localFiles.values()) {
            if (file.getType() == FileInfo.Types.SEED) {
                result.add(file);
            }
        }
        return result;
    }

    public ArrayList<FileInfo> getLeechFiles() {
        ArrayList<FileInfo> result = new ArrayList<>();
        for (FileInfo file : localFiles.values()) {
            if (file.getType() == FileInfo.Types.LEECH) {
                result.add(file);
            }
        }
        return result;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FileInfo file = (new ArrayList<>(currentFiles.values())).get(rowIndex);
        switch (columnIndex) {
            case 0:
                return file.getFileName();
            case 1:
                return file.getLength();
            case 2:
                return file.getPieceSize();
            case 3:
                return file.getKey();
            case 4:
                return file.getType();
            case 5:
                return file.getProgress();
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getRowCount() {
        return currentFiles.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    //@Override
    //public Class getColumnClass(int c) {
    //    return getValueAt(0, c).getClass();
    //}
}
