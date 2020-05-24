package tracker.msg;

import connection.Message;
import file.FileInfoLight;
import java.util.ArrayList;
import utils.ByteTab;
import utils.Logger;

public class FileList extends Message {
    
    private final ArrayList<FileInfoLight> list = new ArrayList<>();

    public FileList(ByteTab s) { //list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2 …]
        super('l');
        for (int i = 1; i < s.length(); i+=4) {
            list.add(new FileInfoLight(s.nextWord(),
                                       s.nextInt(),
                                       s.nextInt(),
                                       s.nextWord()));
        }
        Logger.log("< "+this);
    }
    
    public ArrayList<FileInfoLight> getFiles() {
        return list;
    }
    
}
