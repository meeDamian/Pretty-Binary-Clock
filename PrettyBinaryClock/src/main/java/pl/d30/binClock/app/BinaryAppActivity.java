package pl.d30.binClock.app;

import static pl.d30.binClock.BinaryClockCore.PREF_NAME;
import static pl.d30.binClock.BinaryClockCore.APP;
import static pl.d30.binClock.BinaryClockCore.processProperties;
import pl.d30.binClock.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

public class BinaryAppActivity extends Activity {
	
	private SharedPreferences sp;
	private Boolean newAndroid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		newAndroid = ( Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB ) ? true : false;				
		sp = getSharedPreferences( PREF_NAME + "app", Context.MODE_PRIVATE );
		
		int[] layoutVars = processProperties( sp, APP );
		
		setContentView(R.layout.layout_app);
	}
}
