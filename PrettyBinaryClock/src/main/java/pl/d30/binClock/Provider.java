package pl.d30.binClock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import pl.d30.binClock.view.BinaryClock;
import pl.d30.binClock.view.BinaryClockBCD;
import pl.d30.binClock.view.BinaryClockPure;

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

    public void onUpdate(Context c, AppWidgetManager awm, int[] wids) {
        ArrayList<Widget> widgets = Widget.getValidWidgets(c);
        BinaryTime bt = new BinaryTime();

        for (Widget w : widgets) {
            BinaryClock bc;
            switch (w.getType()) {
                case Widget.TYPE_PURE:
                    bc = new BinaryClockPure(w, bt);
                    break;

                default:
                case Widget.TYPE_BCD:
                    bc = new BinaryClockBCD(w, bt);
                    break;
            }

            awm.updateAppWidget(w.getId(), bc.getRemoteView(c));
        }
    }
}