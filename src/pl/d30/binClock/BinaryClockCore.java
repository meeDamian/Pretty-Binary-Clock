package pl.d30.binClock;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class BinaryClockCore {

	/*
	 * Properties shared across all entities
	 */
	public static final String LOG = "pl.d30.binClock"; // for logging
	public static final String PREF_NAME = "binClock_"; // Prefix for preferences files
	
	public static final int SECOND = 1000;  // isn't that stored somewhere already?
	public static final int MINUTE = 60000; // isn't that stored somewhere already?
	
	// entities ID's
	public static final int APP = 0;
	public static final int WIDGET = 1;
	public static final int WALLPAPER = 2;
	
	// skins ID's
	public static final int SKIN_BYMATKO = 0;
	public static final int SKIN_BYMAURYCY = 1;
	
	// backgrounds ID's
	public static final int BACKGROUND_HEX = -1;
	public static final int BACKGROUND_NONE = 0;
	public static final int BACKGROUND_BLACK = 1;
	public static final int BACKGROUND_WHITE = 2;
	
	// preferences array indexes
	public static final int INTERVAL = 0;
	public static final int BACKGROUND_ID = 1;
	public static final int ON_ID = 2;
	public static final int OFF_ID = 3;
	public static final int DELAY = 4;
	public static final int LAYOUT_ID = 5; 
	
	/**
	 * Converts raw SharedPreferences container into array of preferences filtered by specified 
	 * entity.
	 *  
	 * @param sp - raw SharedPreferences container
	 * @param entity - current's entity ID
	 * @return array of preferences relevant for specified entity
	 */
	public static final int[] processProperties( SharedPreferences sp, int entity ) {
		
		int[] preferences = new int[ 6 ];
		
		// TODO: once it already works change to: int skin = sp.getInt("skin", 0); 
		int skin = Integer.parseInt( sp.getString("skin", "0") );
		
		// TODO: int bkg = sp.getInt("background", BACKGROUND_BLACK);
		int bkg = Integer.parseInt( sp.getString("background", Integer.toString(BACKGROUND_BLACK) ) );

		switch( skin ) {
			case SKIN_BYMAURYCY:				
				preferences[ LAYOUT_ID ] = R.layout.layout_bymaurycy;
				
				if( bkg==BACKGROUND_BLACK ) {					
					preferences[ BACKGROUND_ID ] = R.drawable.bg_black;
					preferences[ ON_ID ] = R.drawable.bymaurycy_black_on;
					preferences[ OFF_ID ] = R.drawable.bymaurycy_black_off;
					
				} else if( bkg==BACKGROUND_WHITE ) {					
					preferences[ BACKGROUND_ID ] = R.drawable.bg_white;
					preferences[ ON_ID ] = R.drawable.bymaurycy_black_on;
					preferences[ OFF_ID ] = R.drawable.bymaurycy_black_off;
					
				} else if( bkg==BACKGROUND_NONE ) {
					preferences[ ON_ID ] = R.drawable.bymaurycy_black_on;
					preferences[ OFF_ID ] = R.drawable.bymaurycy_black_off;
					
				}
				break;
				
			case SKIN_BYMATKO:
				preferences[ LAYOUT_ID ] = R.layout.layout_bymatko;
				
				if( bkg==BACKGROUND_BLACK ) {					
					preferences[ BACKGROUND_ID ] = R.drawable.bg_black;
					preferences[ ON_ID ] = R.drawable.bymatko_black_on;
					preferences[ OFF_ID ] = R.drawable.bymatko_black_off;
					
				} else if( bkg==BACKGROUND_WHITE ) {					
					preferences[ BACKGROUND_ID ] = R.drawable.bg_white;
					preferences[ ON_ID ] = R.drawable.bymatko_white_on;
					preferences[ OFF_ID ] = R.drawable.bymatko_white_off;
					
				} else if( bkg==BACKGROUND_NONE ) {					
					preferences[ ON_ID ] = R.drawable.bymatko_black_on;
					preferences[ OFF_ID ] = R.drawable.bymatko_black_off;
					
				}
				break;
				
		}
		
		boolean seconds = sp.getBoolean("seconds", false);
		if( !seconds ) {
			
			preferences[ INTERVAL ] = MINUTE;
			
			// to make it start just after next full minute
			Calendar cal = Calendar.getInstance();
			cal.set( Calendar.MINUTE, cal.get(Calendar.MINUTE)+1 );
			cal.set( Calendar.SECOND, 0 );
			cal.set( Calendar.MILLISECOND, 0 );
			
			preferences[ DELAY ] = (int) (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
			
		} else preferences[ INTERVAL ] = SECOND;
		
		return preferences;
	}
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static void logWTF(String msg) {
		
		if( Build.VERSION.SDK_INT>= Build.VERSION_CODES.FROYO ) Log.wtf(LOG, msg);
		else Log.e(LOG, msg);
		
	}
	
	
	
}
