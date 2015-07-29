package pl.d30.binClock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;

import static pl.d30.binClock.ClockIntent.BINARY_ALARM_START;
import static pl.d30.binClock.ClockIntent.BINARY_ALARM_STOP;
import static pl.d30.binClock.ClockIntent.BINARY_WIDGET_CHANGE;
import static pl.d30.binClock.ClockIntent.BINARY_WIDGET_REMOVE;
import static pl.d30.binClock.ClockService.sendToService;

public class Provider extends AppWidgetProvider {

    // this litter sucker manages everything that doesn't fit to any other method here
    public void onReceive(@NonNull Context c, @NonNull Intent intent) {
        switch(intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:
            case Intent.ACTION_SCREEN_ON:
                sendToService(c, intent.getAction());
                return;

            case Intent.ACTION_MY_PACKAGE_REPLACED:
            case Intent.ACTION_BOOT_COMPLETED:
                Bundle b = new Bundle();
                b.putBoolean("clean", true);
                sendToService(c, BINARY_ALARM_START, b);
                return;
        }

        super.onReceive(c, intent);
    }
	public void onEnabled(Context c) {
        sendToService(c, BINARY_ALARM_START);
	}
	public void onAppWidgetOptionsChanged(Context c, AppWidgetManager awm, int wid, Bundle newOptions) {
        newOptions.putInt("wid", wid);
        sendToService(c, BINARY_WIDGET_CHANGE, newOptions);
	}
	public void onDeleted(Context c, int[] ids) {
        Bundle b = new Bundle();
        b.putIntArray("wids", ids);
        sendToService(c, BINARY_WIDGET_REMOVE, b);
	}
	public void onDisabled(Context c) {
        // Do not re-wake the service if it's not running
        if (!ClockService.isRunning(c))
            return;

        sendToService(c, BINARY_ALARM_STOP);
	}


    private static final int[][] BIT = {

        // hours
        { R.id.h1_8, R.id.h1_4, R.id.h1_2, R.id.h1_1 },
        { R.id.h0_8, R.id.h0_4, R.id.h0_2, R.id.h0_1 },

        // minutes
        { R.id.m1_8, R.id.m1_4, R.id.m1_2, R.id.m1_1 },
        { R.id.m0_8, R.id.m0_4, R.id.m0_2, R.id.m0_1 },

        // seconds
        { R.id.s1_8, R.id.s1_4, R.id.s1_2, R.id.s1_1 },
        { R.id.s0_8, R.id.s0_4, R.id.s0_2, R.id.s0_1 }
    };

    public void onUpdate(Context c, AppWidgetManager awm, int[] wids) {
        ArrayList<Widget> widgets = Widget.getValidWidgets(c);

        BinaryTime bt = new BinaryTime();

        RemoteViews rv;
        for (Widget w : widgets) {
            rv = new RemoteViews(c.getPackageName(), R.layout.circles);

            if (w.hasBackground())
                rv.setInt(R.id.master_exploder, "setBackgroundResource", w.getBackground());

            rv.setViewVisibility(R.id.seconds, w.requiresSeconds() ? View.VISIBLE : View.GONE);

            int groups = w.requiresSeconds() ? 3 : 2;
            for (int group = 0; group < groups; group++)
                for (int i = 0; i <= 3; i++)
                    for (int j = 0; j <= 1; j++) {
                        int dotId = BIT[2 * group + j][i];
                        rv.setInt(
                            dotId,
                            "setAlpha",
                            w.getAlpha(!bt.get(group, 2 - j)[i])
                        );
                        rv.setInt(
                            dotId,
                            "setColorFilter",
                            c.getResources().getColor(w.getColor())
                        );
                    }

            awm.updateAppWidget(w.getId(), rv);
        }
    }
}