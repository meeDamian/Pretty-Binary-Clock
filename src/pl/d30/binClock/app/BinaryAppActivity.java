package pl.d30.binClock.app;

import static pl.d30.binClock.BinaryClockCore.PREF_NAME;
import pl.d30.binClock.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class BinaryAppActivity extends Activity {
	
	private SharedPreferences sp;
	private Boolean newAndroid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		newAndroid = ( Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB ) ? true : false;				
		sp = getSharedPreferences( PREF_NAME + "app", Context.MODE_PRIVATE );
		
		setContentView(R.layout.layout_app);
		
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );			
	}
	protected void onStart() {
		super.onStart();
		setActive(true);
	}
	
	public void toggleActive(View v) {
		toggleActive();		
	}
	private void toggleActive() {		
		setActive( !sp.getBoolean("active", true) );	
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setActive(Boolean active) {
		
		if( active ) {
			sp.edit().putBoolean("active", true).commit();
			getWindow().clearFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );
			if( newAndroid ) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);			
		} else {
			sp.edit().putBoolean("active", false).commit();
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);			
			if( newAndroid ) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);			
		}
		
	}
}
