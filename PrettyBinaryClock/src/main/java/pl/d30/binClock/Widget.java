package pl.d30.binClock;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;

public class Widget {
    public static final String PREF_PREFIX  = "binClock_";

    public static final int BKG_TRANSPARENT = 0;
    public static final int BKG_BLACK       = 1;
    public static final int BKG_WHITE       = 2;


    private static final int NO_BACKGROUND  = 0;

    private static final String SP_KEY_CONFIGURED   = "configured";
    private static final String SP_KEY_SECONDS      = "seconds";
    private static final String SP_KEY_BACKGROUND   = "background";



    // widget characteristics
    private int id;
    private boolean needsSeconds;
    private @DrawableRes int background = NO_BACKGROUND;
    private @ColorRes int color;


    private Integer minWidth;
    private Integer maxWidth;

    private Integer minHeight;
    private Integer maxHeight;


    // some helper stuff
    private Context context;
    private boolean valid;


    public Widget(Context c, int widgetId) {
        context = c;
        id = widgetId;

        SharedPreferences sp = getPrefs();
        if (sp==null || !sp.getBoolean(SP_KEY_CONFIGURED, false)) {
            valid = false;
            return;
        }

        process(sp);
    }
    public void remove() {
        if (getPrefs()!=null)
            clearPrefs();
    }


    // Processing things
    private void process(SharedPreferences sp) {
        processSize(sp);
        needsSeconds = processSeconds(sp);
        processAppearance(sp);
        valid = true;
    }
    private boolean processSeconds(SharedPreferences sp) {
        boolean seconds = sp.getBoolean(SP_KEY_SECONDS, true);
        return seconds && doSecondsFit();
    }
    private void processAppearance(SharedPreferences sp) {
        switch (getInt(sp, SP_KEY_BACKGROUND, BKG_TRANSPARENT)) {
            case BKG_WHITE:
                background = R.drawable.bg_white;
                color = R.color.dark;
                break;

            case BKG_BLACK:
                background = R.drawable.bg_black;

            default:
            case BKG_TRANSPARENT:
                color = R.color.light;
                break;
        }
    }
    private void processSize(SharedPreferences sp) {
        minHeight = getInteger(sp, "minHeight");
        maxHeight = getInteger(sp, "maxHeight");
        minWidth  = getInteger(sp, "minWidth");
        maxWidth  = getInteger(sp, "maxWidth");
    }


    // SIZE related things
    private boolean doSecondsFit() {
        Integer width = minWidth != null
            ? minWidth
            : maxWidth;

        return width == null || width > 180;
    }
    public void setDimensions(int minH, int maxH, int minW, int maxW) {
        getPrefs()
            .edit()
            .putInt("minHeight", minHeight = minH)
            .putInt("maxHeight", maxHeight = maxH)
            .putInt("minWidth" , minWidth  = minW)
            .putInt("maxWidth" , maxWidth  = maxW)
            .commit();
    }


    public int getId() {
        return id;
    }
    public boolean isValid() {
        return valid;
    }
    public boolean requiresSeconds() {
        return needsSeconds;
    }
    public boolean hasBackground() {
        return background != NO_BACKGROUND;
    }
    public @DrawableRes int getBackground() {
        return background;
    }
    public @ColorRes int getColor() {
        return color;
    }
    public int getAlpha(boolean state) {
        return state ? 255 : 85;
    }



    // SharedPreferences stuff
    private int getInt(SharedPreferences sp, String key, int defaultValue) {
        return Integer.parseInt(sp.getString(key, Integer.toString(defaultValue)));
    }
    private Integer getInteger(SharedPreferences sp, String key) {
        int tmp = sp.getInt(key, -1);
        return tmp==-1 ? null : tmp;
    }
    private String getPrefsName() {
        return getPrefsName(id);
    }
    private SharedPreferences getPrefs() {
        return context.getSharedPreferences(getPrefsName(), Context.MODE_PRIVATE);
    }
    private void clearPrefs() {
        getPrefs()
            .edit()
            .clear()
            .commit();
    }
    public void makeValid() {
        getPrefs()
            .edit()
            .putBoolean(SP_KEY_CONFIGURED, true)
            .commit();
    }


    /*
     * STATIC
     */
    public static String getPrefsName(int id) {
        return PREF_PREFIX + id;
    }
    public static ArrayList<Widget> getValidWidgets(Context c, int[] wids, boolean removeInvalid) {
        ArrayList<Widget> widgets = new ArrayList<>();
        for (int wid : wids) {
            Widget w = new Widget(c, wid);
            if (w.isValid())
                widgets.add(w);

            else if (removeInvalid)
                w.remove();
        }

        return widgets;
    }

    public static ArrayList<Widget> getValidWidgets(Context c, boolean removeInvalid) {
        int[] wids = AppWidgetManager
            .getInstance(c)
            .getAppWidgetIds(new ComponentName(c, Provider.class));

        return getValidWidgets(c, wids, removeInvalid);
    }
    public static ArrayList<Widget> getValidWidgets(Context c) {
        return getValidWidgets(c, false);
    }
    public static void clearInvalidWidgets(Context c) {
        getValidWidgets(c, true);
    }

    public static int[] getIds(ArrayList<Widget> widgets) {
        int[] ids = new int[widgets.size()];

        for(int i = 0, n = widgets.size(); i < n; i++)
            ids[i] = widgets.get(i).getId();

        return ids;
    }
    public static int[] getIds(Context c) {
        return getIds(getValidWidgets(c));
    }

    public static boolean areSecondsRequired(ArrayList<Widget> widgets) {
        for (Widget w : widgets)
            if (w.requiresSeconds())
                return true;

        return false;
    }
    public static boolean areSecondsRequired(Context c) {
        return areSecondsRequired(getValidWidgets(c));
    }

}
