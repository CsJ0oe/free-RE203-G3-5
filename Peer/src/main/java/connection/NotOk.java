package connection;

import connection.Message;

public class NotOk extends Message {

    public NotOk() {
        super('n');
        append("nok");
    }
}
