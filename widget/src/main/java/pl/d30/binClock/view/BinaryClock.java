package pl.d30.binClock.view;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.RemoteViews;

import pl.d30.binClock.BinaryTime;
import pl.d30.binClock.R;
import pl.d30.binClock.Widget;

abstract public class BinaryClock {

    protected static final String METHOD_BACKGROUND   = "setBackgroundResource";
    protected static final String METHOD_COLOR_FILTER = "setColorFilter";
    protected static final String METHOD_ALPHA        = "setImageAlpha";
    protected static final String METHOD_ALPHA_LEGACY = "setAlpha";

    protected Widget      w;
    protected BinaryTime  bt;
    protected RemoteViews rv;

    public BinaryClock(Widget widget, BinaryTime binaryTime) {
        w = widget;
        bt = binaryTime;
    }

    public void prepareRemoteView(Context context) {
        rv = new RemoteViews(context.getPackageName(), getLayout());

        setBackground();
        setVisibility(R.id.seconds, w.requiresSeconds(), true);

        rv.setOnClickPendingIntent(R.id.master_exploder, getIntent(context));
    }

    public abstract RemoteViews getRemoteView(Context context);

    private PendingIntent getIntent(Context context) {
        Intent intent = new Intent(getIntentAction());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    @SuppressLint("InlinedApi")
    private String getIntentAction() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
            ? AlarmClock.ACTION_SHOW_ALARMS
            : AlarmClock.ACTION_SET_ALARM;
    }

    @LayoutRes
    abstract protected int getLayout();

    protected void setColor(@IdRes int dotId) {
        rv.setInt(
            dotId,
            METHOD_COLOR_FILTER,
            w.getColor()
        );
    }

    protected void setAlpha(@IdRes int dotId, boolean state) {
        rv.setInt(
            dotId,
            getRightAlphaKey(),
            w.getAlpha(state)
        );
    }

    protected void hide(@IdRes int dotId) {
        setVisibility(dotId, false);
    }
    protected void show(@IdRes int dotId) {
        setVisibility(dotId, true);
    }
    protected void setVisibility(@IdRes int dotId, boolean state) {
        setVisibility(dotId, state, false);
    }
    protected void setVisibility(@IdRes int dotId, boolean state, boolean useGone) {
        rv.setViewVisibility(dotId, state
                ? View.VISIBLE
                : useGone
                    ? View.GONE
                    : View.INVISIBLE
        );
    }

    protected void setBackground() {
        if (w.hasBackground())
            rv.setInt(R.id.master_exploder, METHOD_BACKGROUND, w.getBackground());
    }

    protected static String getRightAlphaKey() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
            ? METHOD_ALPHA
            : METHOD_ALPHA_LEGACY;
    }
}
