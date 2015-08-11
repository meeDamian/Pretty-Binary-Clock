package pl.d30.binClock.view;


import android.content.Context;
import android.widget.RemoteViews;

import pl.d30.binClock.BinaryTime;
import pl.d30.binClock.R;
import pl.d30.binClock.Widget;

public class BinaryClockBCD extends BinaryClock {

    private static final int[][] BIT = {

        // hours
        {R.id.am_pm, R.id.h1_4, R.id.h1_2, R.id.h1_1},
        {R.id.h0_8, R.id.h0_4, R.id.h0_2, R.id.h0_1},

        // minutes
        {R.id.m1_8, R.id.m1_4, R.id.m1_2, R.id.m1_1},
        {R.id.m0_8, R.id.m0_4, R.id.m0_2, R.id.m0_1},

        // seconds
        {R.id.s1_8, R.id.s1_4, R.id.s1_2, R.id.s1_1},
        {R.id.s0_8, R.id.s0_4, R.id.s0_2, R.id.s0_1}
    };

    public BinaryClockBCD(Widget widget, BinaryTime binaryTime) {
        super(widget, binaryTime);
    }

    @Override
    public RemoteViews getRemoteView(Context c) {
        prepareRemoteView(c);

        int groups = w.requiresSeconds()
            ? 3
            : w.doMinutesFit()
                ? 2
                : 1;

        setVisibility(R.id.minutes, w.doMinutesFit(), true);

        for (int group = 0; group < groups; group++)
            for (int j = 0; j <= 1; j++) {
                boolean[] digit = bt.get(group, 2 - j, w.isAmPm());
                for (int i = 0; i <= 3; i++) {
                    int dotId = BIT[2 * group + j][i];

                    setColor(dotId);

                    if (w.isAmPm() && group == 0 && j == 0) {
                        if (i == 0) {
                            show(dotId);
                            setAlpha(dotId, bt.isPm());
                            continue;

                        } else if (i <= 2) {
                            hide(dotId);
                            continue;
                        }
                    }

                    setAlpha(dotId, digit[i]);
                }
            }

        return rv;
    }

    @Override
    protected int getLayout() {
        return R.layout.circles_bcd;
    }
}
