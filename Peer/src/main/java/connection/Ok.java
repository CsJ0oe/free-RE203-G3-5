package connection;

import connection.Message;

public class Ok extends Message {

    public Ok() {
        super('o');
        append("ok");
    }
}
