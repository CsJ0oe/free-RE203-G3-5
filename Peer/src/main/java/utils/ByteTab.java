package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class ByteTab {

    private int length;
    private int index;
    public byte[] data;

    public ByteTab(byte[] recv) {
        this.data = recv;
        this.index = 0;
        this.length = (new String(recv)).replaceAll("[\\[\\]]", " ").replaceAll("  ", " ").split(" ").length;
    }

    public int length() {
        return length;
    }

    public String nextWord() {
        return nextWord(' ');
    }

    public int nextInt() {
        return nextInt(' ');
    }

    public byte[] nextBytes() {
        return nextBytes(' ');
    }

    public String nextWord(char c) {
        return new String(next(c));
    }

    public int nextInt(char c) {
        return Integer.parseInt(new String(next(c)));
    }

    public byte[] nextBytes(char c) {
        return next(c);
    }

    private byte[] next(char c) {
        ArrayList<Byte> tmp = new ArrayList<>();
        for (; index < data.length; index++) {
            if (data[index] == c) {
                index++;
                break;
            }
            if (data[index] == '[' || data[index] == ']') {
                continue;
            }
            tmp.add(data[index]);
        }
        byte[] res = new byte[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            res[i] = tmp.get(i);
        }
        return res;
    }

}
