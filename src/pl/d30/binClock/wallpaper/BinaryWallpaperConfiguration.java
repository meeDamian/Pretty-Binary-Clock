package pl.d30.binClock.wallpaper;

import pl.d30.binClock.R;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class BinaryWallpaperConfiguration extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences_temp);
		
		Preference circlePreference = getPreferenceScreen().findPreference("numberOfCircles");
		
		circlePreference.setOnPreferenceChangeListener(numberCheckListener);	
	}
	
	Preference.OnPreferenceChangeListener numberCheckListener = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if( newValue!=null && newValue.toString().length()>0 && newValue.toString().matches("\\d*")) return true;
			
			Toast.makeText(BinaryWallpaperConfiguration.this, "Invalid Inut", Toast.LENGTH_SHORT).show();
			return false;
		}
	};
}
