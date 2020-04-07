import java.io.Console;
import java.util.StringTokenizer;

public class CommandHandler extends Thread {

    public void run() {
    	java.io.Console cnsl = System.console();
		while(cnsl != null) {
    		String cmd = cnsl.readLine(">>> ");
    		handleCommand(cmd);
    	}
    }

    public void handleCommand(String cmd) {
    	StringTokenizer st = new StringTokenizer(cmd);
    	if (!st.hasMoreTokens()) {
    		Debug.warning("CommandHandler: empty command");
    		return;
    	}
    	switch(st.nextToken()) {
    		case "leech":
    			Debug.debug("CommandHandler: leech");
    			break;
    		case "seed" :
    			Debug.debug("CommandHandler: seed");
    			break;
    		default:
    			Debug.warning("CommandHandler: unknown command");
    	}
    }

}