package pl.d30.binClock.view;


import android.content.Context;
import android.view.View;
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
        RemoteViews rv = super.getRemoteView(c);

        int groups = w.requiresSeconds()
            ? 3
            : w.doMinutesFit()
                ? 2
                : 1;

        rv.setViewVisibility(R.id.minutes, w.doMinutesFit() ? View.VISIBLE : View.GONE);

        for (int group = 0; group < groups; group++)
            for (int j = 0; j <= 1; j++) {
                boolean[] digit = bt.get(group, 2 - j, w.isAmPm());
                for (int i = 0; i <= 3; i++) {
                    int dotId = BIT[2 * group + j][i];

                    if (group == 0) {
                        if (i == 0 && j == 0) {
                            if (w.isAmPm()) {
                                rv.setTextColor(dotId, w.getColor());
                                rv.setViewVisibility(dotId, bt.isPm()
                                    ? View.VISIBLE
                                    : View.INVISIBLE);
                            }
                            continue;

                        } else if (w.isAmPm() && (i <= 1 || (i == 2 && j == 0))) {
                            rv.setViewVisibility(dotId, View.INVISIBLE);
                            continue;
                        }
                    }

                    rv.setInt(
                        dotId,
                        getRightAlphaKey(),
                        w.getAlpha(digit[i])
                    );
                    rv.setInt(
                        dotId,
                        "setColorFilter",
                        w.getColor()
                    );
                }
            }

        return rv;
    }

    @Override
    protected int getLayout() {
        return R.layout.circles_bcd;
    }
}
