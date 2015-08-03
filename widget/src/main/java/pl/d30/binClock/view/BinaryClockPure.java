package pl.d30.binClock.view;

import android.content.Context;
import android.widget.RemoteViews;

import pl.d30.binClock.BinaryTime;
import pl.d30.binClock.R;
import pl.d30.binClock.Widget;

public class BinaryClockPure extends BinaryClock {

    private static final int[][] BIT = {

        // hours
        {R.id.am_pm, R.id.h16, R.id.h8, R.id.h4, R.id.h2, R.id.h1},

        // minutes
        {R.id.m32, R.id.m16, R.id.m8, R.id.m4, R.id.m2, R.id.m1},

        // seconds
        {R.id.s32, R.id.s16, R.id.s8, R.id.s4, R.id.s2, R.id.s1}
    };

    public BinaryClockPure(Widget widget, BinaryTime binaryTime) {
        super(widget, binaryTime);
    }

    @Override
    public RemoteViews getRemoteView(Context c) {
        prepareRemoteView(c);

        int groups = w.requiresSeconds() ? 3 : 2;
        for (int group = 0; group < groups; group++) {
            boolean[] digit = bt.get(group, BinaryTime.WHOLE_NUMBER, w.isAmPm());
            for (int i = 0; i <= 5; i++) {
                int dotId = BIT[group][i];

                setColor(dotId);

                if (w.isAmPm() && group == 0) {
                    if (i == 0) {
                        show(dotId);
                        setAlpha(dotId, bt.isPm());
                        continue;

                    } else if (i == 1) {
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
        return R.layout.circles_pure;
    }
}
