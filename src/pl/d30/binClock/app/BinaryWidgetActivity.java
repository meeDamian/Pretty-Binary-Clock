package pl.d30.binClock.app;

import static pl.d30.binClock.BinaryClockCore.PREF_NAME;
import static pl.d30.binClock.BinaryClockCore.LOG;
import pl.d30.binClock.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class BinaryWidgetActivity extends Activity {
	
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG,"a");
		
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		sp = getSharedPreferences( PREF_NAME + "app", Context.MODE_PRIVATE );
		
		setContentView(R.layout.layout_app);
		
		getWindow().clearFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );		
		setActive( true );
		
		// NOTE: enabling full screen
		//requestWindowFeature( Window.FEATURE_NO_TITLE );
		//getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

		// NOTE: toggle screen sleeping
		//getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
		//getWindow().clearFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
		
		// NOTE: that makes system UI really low priority; should be used only after android version check		
		
		
	}
	
	public void toggleActive(View v) {
		toggleActive();		
	}
	private void toggleActive() {		
		setActive( !sp.getBoolean("active", true) );	
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setActive(Boolean b) {
		
		if( b ) {
			sp.edit().putBoolean("active", true).commit();
			getWindow().clearFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );
			if( Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB ) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);			
		} else {
			sp.edit().putBoolean("active", false).commit();
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);			
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);			
		}
		
	}
}
