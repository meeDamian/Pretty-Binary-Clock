package pl.d30.binClock;

import java.util.Calendar;

public class BinaryTime {

    public static final int DIGIT_RIGHT = 0b1;  // 1
    public static final int DIGIT_LEFT  = 0b10; // 2
    public static final int WHOLE_NUMBER = 0b11; // 3

    public static final int HOUR = 0;
    public static final int MINUTE = 1;
    public static final int SECOND = 2;

    private int hours;
    private int minutes;
    private int seconds;

    public BinaryTime() {
        Calendar nao = Calendar.getInstance();

        hours = nao.get(Calendar.HOUR_OF_DAY);
        minutes = nao.get(Calendar.MINUTE);
        seconds = nao.get(Calendar.SECOND);
    }

    public boolean[] get(int whichValue, int whichDigit) {
        switch (whichValue) {
            case HOUR:
                return getDigit(hours, whichDigit);
            case MINUTE:
                return getDigit(minutes, whichDigit);
            case SECOND:
                return getDigit(seconds, whichDigit);
            default:
                return null;
        }
    }

    private boolean[] getDigit(int value, int whichDigit) {
        switch (whichDigit) {
            case DIGIT_LEFT:
                return convertToBinary(value / 10, 4);
            case DIGIT_RIGHT:
                return convertToBinary(value % 10, 4);
            case WHOLE_NUMBER:
            default:
                return convertToBinary(value, 6);
        }
    }

    private boolean[] convertToBinary(int value, int digits) {
        boolean[] bits = new boolean[digits];
        for (int i = digits - 1; i >= 0; i--)
            bits[digits - i - 1] = (value & (1 << i)) != 0;

        return bits;
    }
}
