package pl.d30.binClock;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

import static pl.d30.binClock.ClockCore.LOG;
import static pl.d30.binClock.ClockCore.cancelAlarm;
import static pl.d30.binClock.ClockCore.getIdsList;
import static pl.d30.binClock.ClockCore.getPrefs;
import static pl.d30.binClock.ClockCore.prepareWidget;
import static pl.d30.binClock.ClockCore.rescheduleAlarm;
import static pl.d30.binClock.ClockCore.setAlarmRepeating;
import static pl.d30.binClock.ClockCore.updateWidget;

public class Provider extends AppWidgetProvider {

	// called after widget update or phone boot - is it ever called?
	public void onEnabled( Context c ) {
		Log.d(LOG, "Provider.onEnabled()");
		setAlarmRepeating(c);
		super.onEnabled(c);
	}

	public void onUpdate(Context c, AppWidgetManager awm, int[] wids) {
		Log.d(LOG, "Provider.onUpdate("+Arrays.toString(wids)+")");
		rescheduleAlarm(c, awm);
		super.onUpdate(c, awm, wids);
	}

	@Override
	public void onReceive(Context c, Intent intent) {
		Log.d(LOG, "Provider.onReceive("+intent.getAction()+")");

		switch( intent.getAction() ) {
			case Intent.ACTION_MY_PACKAGE_REPLACED:
			case Intent.ACTION_BOOT_COMPLETED:
				for(int wid:getIdsList(c, AppWidgetManager.getInstance(c))) prepareWidget(c, wid);
				return;
		}

		super.onReceive(c, intent);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void onAppWidgetOptionsChanged(Context c, AppWidgetManager awm, int wid, Bundle no) {
		Log.d(LOG, "Provider.onAppWidgetOptionsChanged("+wid+")");

		updateWidget(c, awm, wid, 0, no.getInt("appWidgetMinWidth")>180);

		super.onAppWidgetOptionsChanged(c, awm, wid, no);
	}

	// clears settings file for particular deleted widget
	public void onDeleted(Context c, int[] ids) {
		Log.d(LOG, "Provider.onDeleted()");

		// clear removed widget settings file
		getPrefs(c, ids[0]).edit().clear().commit();
		super.onDeleted(c, ids);
	}

	// called after last widget is removed - does it even work?
	public void onDisabled(Context c) {
		Log.d(LOG, "Provider.onDisable()");
		cancelAlarm(c);
		super.onDisabled(c);
	}




	
}