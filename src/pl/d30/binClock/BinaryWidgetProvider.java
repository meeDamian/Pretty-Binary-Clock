package pl.d30.binClock;

import static pl.d30.binClock.BinaryWidgetLibs.*;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class BinaryWidgetProvider extends AppWidgetProvider {
	
	// called after widget update or phone boot
	public void onEnabled( Context c ) {
		Log.d(LOG, "Provider.onEnable();");
		
		/**
		 *  TODO: detect source of call this should be called only after widget version update or [ I forgot =) ]
		 *  what's most important is that it should not be executed after launching preferences but before accepting them...
		 */

		Intent i = new Intent( c.getApplicationContext(), BinaryWidgetReceiver.class );
		PendingIntent pi = PendingIntent.getBroadcast( c.getApplicationContext(), 0, i, 0 );
		AlarmManager a = (AlarmManager) c.getSystemService( Context.ALARM_SERVICE );
		a.cancel( pi );
		
		a.setRepeating( AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), SECOND, pi );
		
		super.onEnabled( c );		
	}
	
	// called after last widget is removed
	public void onDisabled( Context c) {
		Log.d(LOG, "Provider.onDisable();");
		
		Intent i = new Intent( c.getApplicationContext(), BinaryWidgetReceiver.class );
		PendingIntent pi = PendingIntent.getBroadcast( c.getApplicationContext(), 0, i, 0 );
		AlarmManager a = (AlarmManager) c.getSystemService( Context.ALARM_SERVICE );
		a.cancel( pi );
		
		super.onDisabled(c);
	}
	
	// clears settings file for particular deleted widget
	public void onDeleted( Context c, int[] ids ) {
		Log.d(LOG, "Provider.onDeleted();"); 
		
		// clear removed widget settings file-
		c.getSharedPreferences( PREF_NAME+ids[0], Context.MODE_PRIVATE ).edit().clear().commit();
		
		super.onDeleted(c, ids);
	}
}