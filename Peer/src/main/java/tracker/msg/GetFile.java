package tracker.msg;

import connection.Message;
import utils.Logger;

public class GetFile extends Message {

    public GetFile(String filekey) {
        super('g');
        append("getfile ");
        append(filekey);
        Logger.log("< "+this);
    }
}
