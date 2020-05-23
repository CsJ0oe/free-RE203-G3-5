package tracker.msg;

import connection.Message;
import file.FileInfoLight;
import java.util.ArrayList;

public class FileList extends Message {
    
    private final ArrayList<FileInfoLight> list = new ArrayList<>();

    public FileList(String[] tok) { //list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2 …]
        super('l');
        for (int i = 1; i < tok.length; i+=4) {
            list.add(new FileInfoLight(tok[i].strip(),
                                       Integer.parseInt(tok[i + 1].strip()),
                                       Integer.parseInt(tok[i + 2].strip()),
                                       tok[i + 3].strip()));
        }
    }
    
    public ArrayList<FileInfoLight> getFiles() {
        return list;
    }
    
}
