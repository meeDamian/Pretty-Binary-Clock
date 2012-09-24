package pl.d30.binClock;

import android.content.SharedPreferences;

public class BinaryClockCore {

	/*
	 * Properties shared across all entities
	 */
	static final String LOG = "pl.d30.binClock"; // for logging
	static final String PREF_NAME = "binClock_"; // Prefix for preferences files
	
	static final int SECOND = 1000; // isn't that stored somewhere already?
	static final int MINUTE = 60000;// isn't that stored somewhere already?
	
	// entities ID's
	static final int APP = 0;
	static final int WIDGET = 1;
	static final int WALLPAPER = 2;
	
	// skins ID's
	static final int SKIN_BYMATKO = 0;
	static final int SKIN_BYMAURYCY = 1;
	
	// backgrounds ID's
	static final int BACKGROUND_HEX = -1;
	static final int BACKGROUND_NONE = 0;
	static final int BACKGROUND_BLACK = 1;
	static final int BACKGROUND_WHITE = 2;
	
	/**
	 * Converts raw SharedPreferences container into array of preferences filtered by specified 
	 * entity.
	 *  
	 * @param sp - raw SharedPreferences container
	 * @param entity - current's entity ID
	 * @return array of preferences relevant for specified entity
	 */
	static final int[] processProperties( SharedPreferences sp, int entity ) {
		
		int[] preferences = new int[ 6 ];
		
		int skin = Integer.parseInt( sp.getString("skin", "0") );
		
		
		return null;
	}
	
	
	
}
