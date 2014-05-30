package pl.d30.binClock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Calendar;

public class ClockCore {

	/*
	 * Properties shared across all entities
	 */
	public static final String LOG = "pl.d30.binClock"; // for logging
	public static final String PREF_NAME = "binClock_"; // Prefix for preferences files
	
	public static final int SECOND = 1000;
	public static final int MINUTE = 60 * SECOND;

	
	// backgrounds ID's
	public static final int BACKGROUND_NONE = 0;
	public static final int BACKGROUND_BLACK = 1;
	public static final int BACKGROUND_WHITE = 2;
	
	// preferences array indexes
	public static final int BACKGROUND_ID = 0;
	public static final int ON_ID = 1;
	public static final int OFF_ID = 2;

	static int[] processProperties( SharedPreferences sp ) {
		
		int[] preferences = new int[ 3 ];

		int bkg = Integer.parseInt( sp.getString("background", Integer.toString(BACKGROUND_BLACK) ) );

		if( bkg==BACKGROUND_WHITE ) {
			preferences[ BACKGROUND_ID ] = R.drawable.bg_white;
			preferences[ ON_ID ] = R.drawable.white_on;
			preferences[ OFF_ID ] = R.drawable.white_off;

		} else {
			if( bkg==BACKGROUND_BLACK ) preferences[ BACKGROUND_ID ] = R.drawable.bg_black;
			preferences[ ON_ID ] = R.drawable.black_on;
			preferences[ OFF_ID ] = R.drawable.black_off;
		}
		
		return preferences;
	}

	static SharedPreferences getPrefs(Context c, int widgetId) {
		return c.getSharedPreferences(PREF_NAME + widgetId, Context.MODE_PRIVATE);
	}

	static void prepareWidget(Context c, int wid) {
		SharedPreferences sp = getPrefs(c, wid);
		int[] layoutVars = processProperties( sp );
		updateWidget(c, AppWidgetManager.getInstance( c ), wid, layoutVars[ BACKGROUND_ID ], true);
	}
	static void updateWidget(Context c, AppWidgetManager awm, int wid, int bkg, boolean seconds) {
		RemoteViews rv = new RemoteViews( c.getPackageName(), R.layout.circles );

		if( bkg!=0 ) rv.setInt( R.id.master_exploder, "setBackgroundResource", bkg );

		rv.setViewVisibility( R.id.seconds, seconds ? View.VISIBLE : View.GONE  );
		getPrefs(c, wid).edit().putBoolean("seconds", seconds).commit();

		awm.updateAppWidget( wid, rv );
		rescheduleAlarm(c, awm);
	}

	static int calculateDelay() {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.MINUTE, cal.get(Calendar.MINUTE)+1 );
		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );

		return (int) (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
	}

	static int[] getIdsList(Context c, AppWidgetManager awm) {
		return awm.getAppWidgetIds(new ComponentName(c, Provider.class));
	}

	static boolean secondsRequired(Context c, AppWidgetManager awm) {
		for(int wid : getIdsList(c,awm) )
			if( getPrefs(c, wid).getBoolean("seconds", true) )
				return true;

		return false;
	}

	static AlarmManager getAlarmManager(Context context) {
		return (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
	}
	static PendingIntent getPendingIntent(Context context) {
		return PendingIntent.getBroadcast(context, 0, new Intent(context, Receiver.class), 0);
	}
	static void setAlarmRepeating(Context context, int interval, int delay) {
		getAlarmManager(context)
			.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delay, interval, getPendingIntent(context));
	}
	static void setAlarmRepeating(Context context) {
		setAlarmRepeating(context, SECOND, 0);
	}
	static void cancelAlarm(Context context) {
		getAlarmManager(context)
			.cancel(getPendingIntent(context));
	}
	static void rescheduleAlarm(Context c, AppWidgetManager awm) {
		boolean seconds =  secondsRequired(c, awm);
		setAlarmRepeating(c,
			seconds ? SECOND : MINUTE,
			seconds ? 0 : calculateDelay()
		);
	}

	
}
