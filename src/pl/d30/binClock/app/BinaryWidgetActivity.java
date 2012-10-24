package pl.d30.binClock.app;

import static pl.d30.binClock.BinaryClockCore.PREF_NAME;
import pl.d30.binClock.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class BinaryWidgetActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		SharedPreferences sp = getSharedPreferences( PREF_NAME + "app", Context.MODE_PRIVATE );
		
		setContentView(R.layout.layout_app);
		
		if( sp.getBoolean("fullscreen", false) ) fullscreenOn(sp);
		
		// NOTE: enabling full screen
		//requestWindowFeature( Window.FEATURE_NO_TITLE );
		//getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

		// NOTE: toggle screen sleeping
		//getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
		//getWindow().clearFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
		
		// NOTE: that makes system UI really low priority; should be used only after android version check
		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
		
		
		getWindow().clearFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
	}
	
	public void toggleFullscreen(View v) {		
		SharedPreferences sp = getSharedPreferences( PREF_NAME + "app", Context.MODE_PRIVATE );
		if( sp.getBoolean("fullscreen", false) ) fullscreenOff( sp );
		else fullscreenOn(sp);		
	}	
	private void fullscreenOn(SharedPreferences sp) {
		((ImageButton)findViewById(R.id.fullscreen_toggle)).setImageResource(R.drawable.fullscreen_off);
		sp.edit().putBoolean("fullscreen", true).commit();
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );		
	}
	private void fullscreenOff(SharedPreferences sp) {
		((ImageButton)findViewById(R.id.fullscreen_toggle)).setImageResource(R.drawable.fullscreen_on);
		sp.edit().putBoolean("fullscreen", false).commit();
		getWindow().clearFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );
	}
	
	public void toggleStayawake(View v) {
		
		SharedPreferences sp = getSharedPreferences( PREF_NAME + "app", Context.MODE_PRIVATE );
		//if()
	}

}
