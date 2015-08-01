package pl.d30.binClock.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.RemoteViews;

import pl.d30.binClock.BinaryTime;
import pl.d30.binClock.R;
import pl.d30.binClock.Widget;

abstract public class BinaryClock {

    protected Widget w;
    protected BinaryTime bt;

    public BinaryClock(Widget widget, BinaryTime binaryTime) {
        w = widget;
        bt = binaryTime;
    }
    public RemoteViews getRemoteView(Context context) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), getLayout());

        if (w.hasBackground())
            rv.setInt(R.id.master_exploder, "setBackgroundResource", w.getBackground());

        rv.setViewVisibility(R.id.seconds, w.requiresSeconds() ? View.VISIBLE : View.GONE);

        return rv;
    }

    abstract protected @LayoutRes int getLayout();

    protected static String getRightAlphaKey() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
            ? "setImageAlpha"
            : "setAlpha";
    }
}
