package pl.d30.binClock;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import static pl.d30.binClock.ClockCore.BACKGROUND_ID;
import static pl.d30.binClock.ClockCore.OFF_ID;
import static pl.d30.binClock.ClockCore.ON_ID;
import static pl.d30.binClock.ClockCore.getIdsList;
import static pl.d30.binClock.ClockCore.getPrefs;
import static pl.d30.binClock.ClockCore.processProperties;

public class Receiver extends BroadcastReceiver {
	
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

	@Override
	public void onReceive(Context c, Intent intent) {

		Calendar nao = Calendar.getInstance();
		
		int[] map = {
			nao.get( Calendar.HOUR_OF_DAY ),
			nao.get( Calendar.MINUTE ),
			nao.get( Calendar.SECOND )
		};
		
		AppWidgetManager awm = AppWidgetManager.getInstance(c);
		int[] allIds = getIdsList(c, awm);
		
		if( allIds==null ) {
			Log.wtf(ClockCore.LOG, "Alarm is running with no widgets added.");
			// TODO: stop alarm

		} else {
			RemoteViews rv;
			
			// for each widget added on screen
			for(int wid : allIds) {

				SharedPreferences sp = getPrefs(c, wid);
				int[] layoutVars = processProperties( sp );
				
				rv = new RemoteViews(c.getPackageName(), R.layout.circles );
				
				if( layoutVars[ BACKGROUND_ID ]!=0 ) rv.setInt( R.id.master_exploder, "setBackgroundResource", layoutVars[ BACKGROUND_ID ] );
				
				// for each required group				
				int iterations = ( sp.getBoolean("seconds", true) ) ? 3 : 2 ;
				for( int count = 0; count<iterations; count++ ) {
					
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

				awm.partiallyUpdateAppWidget(wid, rv);
			}
		}
	}
}
