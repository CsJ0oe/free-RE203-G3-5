package connection;

import utils.Logger;

public class NotOk extends Message {

    public NotOk() {
        super('n');
        append("nok");
        Logger.log("> nok");
    }
}
