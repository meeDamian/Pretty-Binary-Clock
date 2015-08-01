package pl.d30.binClock;

import java.util.Calendar;

public class BinaryTime {

    public static final int DIGIT_RIGHT  = 0b1;  // 1
    public static final int DIGIT_LEFT   = 0b10; // 2
    public static final int WHOLE_NUMBER = 0b11; // 3

    public static final int HOUR   = 0;
    public static final int MINUTE = 1;
    public static final int SECOND = 2;

    private int hours;
    private int minutes;
    private int seconds;
    private boolean pm;

    public BinaryTime() {
        Calendar nao = Calendar.getInstance();

        hours = nao.get(Calendar.HOUR_OF_DAY);
        minutes = nao.get(Calendar.MINUTE);
        seconds = nao.get(Calendar.SECOND);

        pm = isItPm(hours);
    }

    public boolean[] get(int whichValue, int whichDigit, boolean am_pm) {
        switch (whichValue) {
            case HOUR:
                int hour = am_pm
                    ? breakTheHour(hours)
                    : hours;
                return getDigit(hour, whichDigit);

            case MINUTE:
                return getDigit(minutes, whichDigit);

            case SECOND:
                return getDigit(seconds, whichDigit);

            default:
                return null;
        }
    }
    public boolean isPm() {
        return pm;
    }

    private static int breakTheHour(int hour) {
        if (hour == 0)
            return 12;

        if (hour > 12)
            return hour % 12;

        return hour;
    }
    private boolean isItPm(int hour) {
        return hour == 12 || hour != 0 && hour > 12;
    }

    private static boolean[] getDigit(int value, int whichDigit) {
        if (whichDigit == DIGIT_LEFT)
            value /= 10;

        if (whichDigit == DIGIT_RIGHT)
            value %= 10;

        int digits = whichDigit == WHOLE_NUMBER
            ? 6
            : 4;

        return convertToBinary(value, digits);
    }

    private static boolean[] convertToBinary(int value, int digits) {
        boolean[] bits = new boolean[digits];
        for (int i = digits - 1; i >= 0; i--)
            bits[digits - i - 1] = (value & (1 << i)) != 0;

        return bits;
    }
}
