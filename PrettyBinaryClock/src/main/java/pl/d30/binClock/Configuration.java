package pl.d30.binClock;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import static pl.d30.binClock.ClockCore.LOG;
import static pl.d30.binClock.ClockCore.PREF_NAME;
import static pl.d30.binClock.ClockCore.prepareWidget;

public class Configuration extends PreferenceActivity {

	private static int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;

	private Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG, "Config.onCreate()");

		c = getApplicationContext();

		Bundle extras = getIntent().getExtras();
		if( extras!=null ) widgetID = extras.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
		if( widgetID==AppWidgetManager.INVALID_APPWIDGET_ID ) {
			
			PreferenceManager.setDefaultValues( c, PREF_NAME+widgetID, MODE_PRIVATE, R.xml.preferences, false);
			
			// TODO: update background summary upon value change 
			
			setResult( RESULT_CANCELED );
			finish();
		}

		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, new BinaryWidgetSettings())
			.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(LOG, "Config.onCreateOptionsMenu();");
		getMenuInflater().inflate(R.menu.preferences, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(LOG,"Config.onOptionsItemSelected();");

		return item.getItemId()==R.id.menu_done
			? createWidget()
			: super.onOptionsItemSelected(item);
	}

	private boolean createWidget() {
		prepareWidget(c, widgetID);
						
		/// return widget ID
		Intent resultValue = new Intent();
		resultValue.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID );
		setResult( RESULT_OK, resultValue );
		
		finish();
		return true;
	}

	public static class BinaryWidgetSettings extends PreferenceFragment {
		
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			Log.d(LOG, "Fragment.onCreate();");

			getPreferenceManager().setSharedPreferencesName(PREF_NAME + widgetID);
			
			addPreferencesFromResource(R.xml.preferences);
		}
		
	}

}
