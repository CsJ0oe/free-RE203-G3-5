package tracker.msgs;

import utils.Message;

public class GetFile extends Message {

    public GetFile(String filekey) {
        append("getfile ");
        append(filekey);
    }
}
