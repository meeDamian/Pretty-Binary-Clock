package pl.d30.binClock;

import static pl.d30.binClock.BinaryWidgetLibs.BACKGROUND_ID;
import static pl.d30.binClock.BinaryWidgetLibs.DELAY;
import static pl.d30.binClock.BinaryWidgetLibs.INTERVAL;
import static pl.d30.binClock.BinaryWidgetLibs.LAYOUT_ID;
import static pl.d30.binClock.BinaryWidgetLibs.LOG;
import static pl.d30.binClock.BinaryWidgetLibs.MINUTE;
import static pl.d30.binClock.BinaryWidgetLibs.PREF_NAME;
import static pl.d30.binClock.BinaryWidgetLibs.processSettings;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class BinaryWidgetConfiguration extends PreferenceActivity {	
	
	private static int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG, "Config.onCreate();");
		
		
		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if( extras!=null ) widgetID = extras.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
		if( widgetID==AppWidgetManager.INVALID_APPWIDGET_ID ) {
			
			PreferenceManager.setDefaultValues( getApplicationContext(), PREF_NAME+widgetID, MODE_PRIVATE, R.xml.preferences, false);
			
			// TODO: update background summary upon value change 
			
			setResult( RESULT_CANCELED );
			finish();
			
		}
		
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			
			getFragmentManager().beginTransaction().replace( android.R.id.content, new BinaryWidgetSettings() ).commit();
			
		} else {
			
			PreferenceManager localPrefs = getPreferenceManager();
			localPrefs.setSharedPreferencesName( PREF_NAME+widgetID );
			addPreferencesFromResource( R.xml.preferences );
			
			Toast.makeText(getApplicationContext(), R.string.legacy_done, Toast.LENGTH_LONG).show();

		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(LOG, "Config.onCreateOptionsMenu();");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.preferences, menu );
		return true;
	}
	

	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ){
		Log.d(LOG, key);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(LOG,"Config.onOptionsItemSelected();");
				
		switch( item.getItemId() ){
			case R.id.menu_done: createWidget(); return true;				
			default: return super.onOptionsItemSelected( item );
		}
	}
	
	@Override
	public void onBackPressed() {
		if( Build.VERSION.SDK_INT<=Build.VERSION_CODES.HONEYCOMB) createWidget();
		super.onBackPressed();
	}
	
	private void createWidget() {
		Context c = getApplicationContext() ;
		SharedPreferences sp = getSharedPreferences( PREF_NAME+widgetID, MODE_PRIVATE );
		
		int[] layoutVars = processSettings( sp );
		
		// TODO: save to common preferencesFile current number of widgets that need to be updated every second
		// TODO: save to common preferencesFile default action triggered by widget click
		// SharedPreferences common = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		
		// Set layout
		AppWidgetManager awm = AppWidgetManager.getInstance( c );
		RemoteViews rv = new RemoteViews( c.getPackageName(), layoutVars[ LAYOUT_ID ] );
		
		if( layoutVars[ BACKGROUND_ID ]!=0 ) rv.setInt( R.id.master_exploder, "setBackgroundResource", layoutVars[ BACKGROUND_ID ] );
		if( layoutVars[ INTERVAL ]==MINUTE ) rv.setViewVisibility( R.id.seconds, View.GONE );
		awm.updateAppWidget( widgetID, rv );
		
		// TODO: b4 starting alarm run intent once
		
		// start alarm manager running this particular widget
		Intent i = new Intent( c.getApplicationContext(), BinaryWidgetReceiver.class );
		PendingIntent pi = PendingIntent.getBroadcast( c.getApplicationContext(), 0, i, 0 );
		AlarmManager a = (AlarmManager) c.getSystemService( Context.ALARM_SERVICE );
		a.setRepeating( AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + layoutVars[ DELAY ], layoutVars[ INTERVAL ], pi );		
						
		/// return widget ID
		Intent resultValue = new Intent();
		resultValue.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID );
		setResult( RESULT_OK, resultValue );
		
		finish();
	}
	
	@TargetApi(11)
	public static class BinaryWidgetSettings extends PreferenceFragment {
		
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			Log.d(LOG,"Fragment.onCreate();");
			
			PreferenceManager localPrefs = getPreferenceManager();
			localPrefs.setSharedPreferencesName( PREF_NAME+widgetID );
			
			addPreferencesFromResource( R.xml.preferences );
		}
		
	}

}
