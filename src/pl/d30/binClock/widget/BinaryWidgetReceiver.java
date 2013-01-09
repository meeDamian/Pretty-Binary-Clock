package pl.d30.binClock.widget;

import static pl.d30.binClock.BinaryClockCore.WIDGET;
import static pl.d30.binClock.BinaryClockCore.BACKGROUND_ID;
import static pl.d30.binClock.BinaryClockCore.LAYOUT_ID;
import static pl.d30.binClock.BinaryClockCore.LOG;
import static pl.d30.binClock.BinaryClockCore.OFF_ID;
import static pl.d30.binClock.BinaryClockCore.ON_ID;
import static pl.d30.binClock.BinaryClockCore.PREF_NAME;
import static pl.d30.binClock.BinaryClockCore.processProperties;

import java.util.Calendar;

import pl.d30.binClock.R;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

public class BinaryWidgetReceiver extends BroadcastReceiver {
	
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

	@TargetApi(11)
	@Override
	public void onReceive(Context context, Intent intent) {
		// Log.d(LOG,"Receiver.onReceive()");

		Calendar nao = Calendar.getInstance();
		
		int[] map = {
			nao.get( Calendar.HOUR_OF_DAY ),
			nao.get( Calendar.MINUTE ),
			nao.get( Calendar.SECOND )
		};
		
		AppWidgetManager awm = AppWidgetManager.getInstance( context.getApplicationContext() );
		ComponentName thisWidget = new ComponentName( context.getApplicationContext(), BinaryWidgetProvider.class );
		int[] allIds = awm.getAppWidgetIds( thisWidget );
		
		if( allIds==null ) {
			Log.wtf(LOG, "Alarm is running with no widgets added.");

		} else if( allIds!=null ) {
			RemoteViews rv;
			
			// for each widget added on screen
			for( int wid : allIds ) {
				
				SharedPreferences sp = context.getSharedPreferences( PREF_NAME+wid, Context.MODE_PRIVATE );
				int[] layoutVars = processProperties( sp, WIDGET );
				
				rv = new RemoteViews( context.getPackageName(), layoutVars[ LAYOUT_ID ] );
				
				if( layoutVars[ BACKGROUND_ID ]!=0 ) rv.setInt( R.id.master_exploder, "setBackgroundResource", layoutVars[ BACKGROUND_ID ] );
				
				// for each required group				
				int iters = ( sp.getBoolean("seconds", false) ) ? 3 : 2 ;
				for( int count = 0; count<iters; count++ ) {
					
					String[] x = {
						String.format( "%4s", Integer.toBinaryString( map[count]/10 ) ).replace(' ', '0'),
						String.format( "%4s", Integer.toBinaryString( map[count]%10 ) ).replace(' ', '0')
					};
					
					for( int j=0; j<=1; j++ ) { // for each decimal digit in number
						for( int i=0; i<=3; i++ ) { // for each binary digit (each square/circle in column)
							rv.setImageViewResource( BIT[2*count+j][i], (x[j].charAt(i)=='1') ? layoutVars[ ON_ID ] : layoutVars[ OFF_ID ] );
						}
					}
					
				}
				
				
				// TODO: check if this assigning of a click cannot be moved somewhere else (someplace it won't be executed every second...)
				PackageManager pm = context.getPackageManager();
				Intent alarmClockIntent = new Intent( Intent.ACTION_MAIN ).addCategory( Intent.CATEGORY_LAUNCHER );
				
				String clockImpls[][] = {
					{"Standard Alarm", "com.android.alarmclock", "com.android.alarmclock.AlarmClock"},
			        {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
			        {"Standard Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
			        {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.AlarmClock"},
			        {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
			        {"Samsung Galaxy Clock", "com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage"}
				};
				
				boolean foundClockImpl = false;
				
				for( int i=0; i<clockImpls.length; i++ ){
					// String vendor = clockImpls[i][0];
					String packageName = clockImpls[i][1];
					String className = clockImpls[i][2];
					
					// TODO: show Toast saying: "To change widget proporties just dump this and add new one!" after 1, 3, 10, 30, 100 click

					try {
						ComponentName cn = new ComponentName( packageName, className );
						pm.getActivityInfo( cn, PackageManager.GET_META_DATA );
						alarmClockIntent.setComponent( cn );
						// Log.d( LOG, "Found " + vendor + " --> " + packageName + "/" + className );
						foundClockImpl = true;
						
					} catch( NameNotFoundException e ) {
						// Log.d(LOG, vendor + " does not exists");
					}
				}
				
				if( foundClockImpl ) {
					PendingIntent pi = PendingIntent.getActivity(context, 0, alarmClockIntent, 0);
					rv.setOnClickPendingIntent( R.id.master_exploder, pi );
				}

				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) awm.partiallyUpdateAppWidget(wid, rv);
				else awm.updateAppWidget(wid, rv);
			}
		}
	}
}
