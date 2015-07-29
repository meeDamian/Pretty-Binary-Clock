package pl.d30.binClock;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static pl.d30.binClock.ClockService.sendToService;

public class Configuration extends PreferenceActivity {

	private int widgetId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();

        widgetId = extras != null
            ? widgetId = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
            : INVALID_APPWIDGET_ID;

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		setResult(RESULT_CANCELED, resultValue);

		if(widgetId == INVALID_APPWIDGET_ID)
			finish();

        BinaryWidgetSettings bws = new BinaryWidgetSettings();
        Bundle b = new Bundle();
        b.putInt(EXTRA_APPWIDGET_ID, widgetId);
        bws.setArguments(b);

		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, bws)
			.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.preferences, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
            case android.R.id.home:
                return createWidget();

            default:
                return super.onOptionsItemSelected(item);
        }
	}

	private boolean createWidget() {
        Bundle b = new Bundle();
        b.putInt("wid", widgetId);
        sendToService(this, ClockIntent.BINARY_WIDGET_CREATE, b);

        // "Phantom widgets" protection
		new Widget(this, widgetId).makeValid();

		Intent resultValue = new Intent();
		resultValue.putExtra(EXTRA_APPWIDGET_ID, widgetId);
		setResult(RESULT_OK, resultValue);
		
		finish();
		return true;
	}

	public static class BinaryWidgetSettings extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle b = getArguments();
            int widgetId = b!=null
                ? b.getInt(EXTRA_APPWIDGET_ID)
                : INVALID_APPWIDGET_ID;

			getPreferenceManager().setSharedPreferencesName(Widget.getPrefsName(widgetId));
			
			addPreferencesFromResource(R.xml.preferences);
		}
	}

}
