package connection;

import utils.Logger;

public class Ok extends Message {

    public Ok() {
        super('o');
        append("ok");
        Logger.log("> ok");
    }
}
