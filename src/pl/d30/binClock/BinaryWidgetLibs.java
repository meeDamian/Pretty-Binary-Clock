package pl.d30.binClock;

import java.util.Calendar;

import android.content.SharedPreferences;

public abstract class BinaryWidgetLibs {
	
	static final String LOG = "pl.d30.binClock";
	static final String PREF_NAME = "binClock_";
	
	static final int SECOND = 1000;
	static final int MINUTE = 60000;

	static final int BACKGROUND_NONE = 0;
	static final int BACKGROUND_BLACK = 1;
	static final int BACKGROUND_WHITE = 2;
	
	static final int SKIN_BYMATKO = 0;
	static final int SKIN_BYMAURYCY = 1;
	
	static final int INTERVAL = 0;
	static final int BACKGROUND_ID = 1;
	static final int ON_ID = 2;
	static final int OFF_ID = 3;
	static final int DELAY = 4;
	static final int LAYOUT_ID = 5; 
	
	static final int[] processSettings( SharedPreferences sp ) {
		
		int[] layoutVars = new int[ 6 ];
		
		int skin = Integer.parseInt( sp.getString("skin", "0") );
		int bgId = Integer.parseInt( sp.getString("background", Integer.toString(BACKGROUND_BLACK) ) );
		switch( skin ) {
			case SKIN_BYMAURYCY:
				layoutVars[ LAYOUT_ID ] = R.layout.layout_bymaurycy;
				layoutVars[ BACKGROUND_ID ] = R.drawable.bg_black;
				layoutVars[ ON_ID ] = R.drawable.bymaurycy_black_on;
				layoutVars[ OFF_ID ] = R.drawable.bymaurycy_black_off;
				break;

			default:
			case SKIN_BYMATKO:
				layoutVars[ LAYOUT_ID ] = R.layout.layout_bymatko;
				
				if( bgId==BACKGROUND_BLACK ) {
					
					layoutVars[ BACKGROUND_ID ] = R.drawable.bg_black;
					layoutVars[ ON_ID ] = R.drawable.bymatko_black_on;
					layoutVars[ OFF_ID ] = R.drawable.bymatko_black_off;
					
				} else if( bgId==BACKGROUND_WHITE ) {
					
					layoutVars[ BACKGROUND_ID ] = R.drawable.bg_white;
					layoutVars[ ON_ID ] = R.drawable.bymatko_white_on;
					layoutVars[ OFF_ID ] = R.drawable.bymatko_white_off;
					
				} else if( bgId==BACKGROUND_NONE ) {
					
					layoutVars[ ON_ID ] = R.drawable.bymatko_black_on;
					layoutVars[ OFF_ID ] = R.drawable.bymatko_black_off;
					
				}
				
				break;
		}
		
		// set background
		
		/*if( bgId!=BACKGROUND_DEFAULT ) {
			// background color change should also trigger on|off images changes
			switch( bgId ) {
				case BACKGROUND_BLACK: layoutVars[ BACKGROUND_ID ] = R.drawable.bg_black; break;
				case BACKGROUND_WHITE: 
				case BACKGROUND_NONE: default: layoutVars[ BACKGROUND_ID ] = 0; break;
			}
		}*/

		boolean seconds = sp.getBoolean("seconds", false);
		if( !seconds ) {
			
			layoutVars[ INTERVAL ] = MINUTE;
			
			// to make it start just after next full minute
			Calendar cal = Calendar.getInstance();
			cal.set( Calendar.MINUTE, cal.get(Calendar.MINUTE)+1 );
			cal.set( Calendar.SECOND, 0 );
			cal.set( Calendar.MILLISECOND, 0 );
			
			layoutVars[ DELAY ] = (int) (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
			
		} else layoutVars[ INTERVAL ] = SECOND;
		
		return layoutVars;
	}

}
