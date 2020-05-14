package utils;

import gui.MainWindow;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

public class FileManager extends AbstractTableModel {
    
    private final LinkedHashMap<String, FileInfo> localFiles = new LinkedHashMap<>();
    private final LinkedHashMap<String, FileInfo> remoteFiles = new LinkedHashMap<>();
    private LinkedHashMap<String, FileInfo> currentFiles;
    String[] columnNames = {"File",
                            "Length",
                            "Piece Size",
                            "Hash",
                            "Type"};
    
    public FileManager() {
        this.add(new FileInfo("file_a.dat", 2097152, 1024, "8905e92afeb80fc7722ec89eb0bf0966", FileInfo.Types.SEED));
        this.add(new FileInfo("file_b.dat", 3145728, 1536, "330a57722ec8b0bf09669a2b35f88e9e", FileInfo.Types.SEED));
        currentFiles = localFiles;
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
            case 0: return file.getName();
            case 1: return file.getLength();
            case 2: return file.getPieceSize();
            case 3: return file.getKey();
            case 4: return file.getType();
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