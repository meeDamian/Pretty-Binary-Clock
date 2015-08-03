package pl.d30.binClock.view;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
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

    protected Widget     w;
    protected BinaryTime bt;

    public BinaryClock(Widget widget, BinaryTime binaryTime) {
        w = widget;
        bt = binaryTime;
    }

    public RemoteViews getRemoteView(Context context) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), getLayout());

        if (w.hasBackground())
            rv.setInt(R.id.master_exploder, METHOD_BACKGROUND, w.getBackground());

        rv.setViewVisibility(R.id.seconds, w.requiresSeconds() ? View.VISIBLE : View.GONE);

        rv.setOnClickPendingIntent(R.id.master_exploder, getIntent(context));

        return rv;
    }

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

    protected static String getRightAlphaKey() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
            ? METHOD_ALPHA
            : METHOD_ALPHA_LEGACY;
    }
}
