package pl.d30.binClock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class ColorPickerPreference extends Preference {

    protected boolean alphaSlider;
    protected boolean lightSlider;

    protected int selectedColor = 0;

    protected ColorPickerView.WHEEL_TYPE wheelType;
    protected int density;

    private String pickerTitle;

    protected ImageView colorIndicator;


    public ColorPickerPreference(Context context) {
        super(context);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWith(context, attrs);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWith(context, attrs);
    }


    private void initWith(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference);

        try {
            alphaSlider = typedArray.getBoolean(R.styleable.ColorPickerPreference_alphaSlider, false);
            lightSlider = typedArray.getBoolean(R.styleable.ColorPickerPreference_lightnessSlider, false);

            density = typedArray.getInt(R.styleable.ColorPickerPreference_density, 10);
            wheelType = ColorPickerView.WHEEL_TYPE.indexOf(typedArray.getInt(R.styleable.ColorPickerPreference_wheelType, 0));

            pickerTitle = typedArray.getString(R.styleable.ColorPickerPreference_pickerTitle);

            selectedColor = typedArray.getInt(R.styleable.ColorPickerPreference_initialColor, 0xffffffff);

        } finally {
            typedArray.recycle();
        }

        setWidgetLayoutResource(R.layout.chosen_color);
    }


    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        Resources res = view.getContext().getResources();
        GradientDrawable colorChoiceDrawable = null;

        colorIndicator = (ImageView) view.findViewById(R.id.color_indicator);

        Drawable currentDrawable = colorIndicator.getDrawable();
        if (currentDrawable!=null && currentDrawable instanceof GradientDrawable)
            colorChoiceDrawable = (GradientDrawable) currentDrawable;

        if (colorChoiceDrawable==null) {
            colorChoiceDrawable = new GradientDrawable();
            colorChoiceDrawable.setShape(GradientDrawable.OVAL);
        }

        int darkenedColor = Color.rgb(
            Color.red(  selectedColor) * 192 / 256,
            Color.green(selectedColor) * 192 / 256,
            Color.blue( selectedColor) * 192 / 256
        );

        colorChoiceDrawable.setColor(selectedColor);
        colorChoiceDrawable.setStroke((int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1,
            res.getDisplayMetrics()
        ), darkenedColor);

        colorIndicator.setImageDrawable(colorChoiceDrawable);
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            selectedColor = value;
            persistInt(value);
            notifyChanged();
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }


    @Override
    protected void onClick() {
        ColorPickerDialogBuilder builder = ColorPickerDialogBuilder
            .with(getContext())
            .setTitle(pickerTitle)
            .initialColor(selectedColor)
            .wheelType(wheelType)
            .density(density)
            .setPositiveButton("ok", new ColorPickerClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selectedColorFromPicker, Integer[] allColors) {
                setValue(selectedColorFromPicker);
                }
            })
            .setNegativeButton("cancel", null);

        if (!alphaSlider && !lightSlider)
            builder.noSliders();

        // NOTE: we know that at least one is enabled
        if (!alphaSlider) builder.lightnessSliderOnly();
        else builder.alphaSliderOnly();


        builder
            .build()
            .show();
    }


//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Parcelable parcelable = super.onSaveInstanceState();
//        if (isPersistent())
//            return parcelable;
//
//        final SavedState savedState = new SavedState(parcelable);
//        savedState.setSelectedColor(this.selectedColor);
//        return savedState;
//    }


//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        if (state.getClass().equals(SavedState.class)) {
//            SavedState savedState = (SavedState) state;
//            super.onRestoreInstanceState(savedState.getSuperState());
//            this.selectedColor = savedState.getSelectedColor();
//            updateColorIndicator();
//            notifyChanged();
//        }
//
//        super.onRestoreInstanceState(state);
//    }


//    static class SavedState extends BaseSavedState {
//        private int selectedColor;
//
//
//        @SuppressWarnings("unused")
//        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
//            public SavedState createFromParcel(Parcel in) {
//                return new SavedState(in);
//            }
//
//            public SavedState[] newArray(int size) {
//                return new SavedState[size];
//            }
//        };
//
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            super.writeToParcel(dest, flags);
//            dest.writeInt(selectedColor);
//        }
//
//
//        public SavedState(Parcel source) {
//            super(source);
//            selectedColor = source.readInt();
//        }
//
//
//        public SavedState(Parcelable superState) {
//            super(superState);
//        }
//
//
//        public void setSelectedColor(int selectedColor) {
//            this.selectedColor = selectedColor;
//        }
//
//
//        public int getSelectedColor() {
//            return selectedColor;
//        }
//    }
}
