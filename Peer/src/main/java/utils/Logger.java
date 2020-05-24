
package utils;



public class Logger {
    
    public enum Levels {INFO, WARNING, ERROR};
    
    public static void log(String s) {
        Globals.logArea.append(s+"\n");
    }

}
