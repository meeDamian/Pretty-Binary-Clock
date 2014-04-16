package pl.d30.binClock.widget;

import static pl.d30.binClock.BinaryClockCore.DELAY;
import static pl.d30.binClock.BinaryClockCore.INTERVAL;
import static pl.d30.binClock.BinaryClockCore.LOG;
import static pl.d30.binClock.BinaryClockCore.PREF_NAME;
import static pl.d30.binClock.BinaryClockCore.SECOND;

import java.util.Arrays;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

public class BinaryWidgetProvider extends AppWidgetProvider {
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void onAppWidgetOptionsChanged( Context c, AppWidgetManager awm, int wid, Bundle no ) {
		Log.d(LOG, "Provider.onAppWidgetOptionsChanged("+wid+");");
		
		super.onAppWidgetOptionsChanged(c, awm, wid, no);
	}

	// clears settings file for particular deleted widget
	public void onDeleted( Context c, int[] ids ) {
		Log.d(LOG, "Provider.onDeleted();");
		
		// clear removed widget settings file-
		c.getSharedPreferences( PREF_NAME+ids[0], Context.MODE_PRIVATE ).edit().clear().commit();		
		super.onDeleted(c, ids);
	}

	// called after last widget is removed - does it even work?
	public void onDisabled( Context c) {
		Log.d(LOG, "Provider.onDisable();");
		
		Intent i = new Intent( c.getApplicationContext(), BinaryWidgetReceiver.class );
		PendingIntent pi = PendingIntent.getBroadcast( c.getApplicationContext(), 0, i, 0 );
		AlarmManager a = (AlarmManager) c.getSystemService( Context.ALARM_SERVICE );
		a.cancel( pi );
		
		super.onDisabled( c );
	}
	
	// called after widget update or phone boot - is it ever called?
	public void onEnabled( Context c ) {
		Log.d(LOG, "Provider.onEnabled();");
		
		/**
		 *  TODO: detect source of call this should be called only after widget version update or reboot
		 *  what's most important is that it should not be executed after launching preferences but before accepting them...
		 */

		Intent i = new Intent( c, BinaryWidgetReceiver.class );
		PendingIntent pi = PendingIntent.getBroadcast( c, 0, i, 0 );
		AlarmManager a = (AlarmManager) c.getSystemService( Context.ALARM_SERVICE );		
		a.setRepeating( AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), SECOND, pi );
		
		super.onEnabled( c );		
	}
	
	public void onReceive(Context c, Intent i ) {
		Log.d(LOG, "Provider.onReceive("+i.getAction()+");");
		super.onReceive(c, i);
	}
	
	public void onUpdate(Context c, AppWidgetManager awm, int[] wids) {
		Log.d(LOG, "Provider.onUpdate("+Arrays.toString(wids)+");");
		
		// TODO: if at least one of widgets in wids requires second interval -set alarm to seconds, else set to minutes
		// TODO: widgets MUST be initialized here! After reboot, app update, or anything
		Intent i = new Intent( c, BinaryWidgetReceiver.class );
		PendingIntent pi = PendingIntent.getBroadcast( c, 0, i, 0 );
		AlarmManager a = (AlarmManager) c.getSystemService( Context.ALARM_SERVICE );		
		a.setRepeating( AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), SECOND, pi );
		
		super.onUpdate(c, awm, wids);
	}
	
	
}