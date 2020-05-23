package tracker.msg;

import connection.Message;

public class GetFile extends Message {

    public GetFile(String filekey) {
        super('g');
        append("getfile ");
        append(filekey);
    }
}
