package com.zybooks.weighttracker;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class HeightPickerMetricPreference extends DialogPreference {

    // Allowed range for user heights in centimeters
    public static final int maxHeightCm = 241;
    public static final int minHeightCm = 122;

    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker picker;
    private int value;

    public HeightPickerMetricPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightPickerMetricPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* Define Height Pickers */
    @Override
    protected View onCreateDialogView() {

        // Define layout
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        picker = new NumberPicker(getContext());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50, 50);
        layoutParams.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        // Define centimeter values for picker
        int totalCm = maxHeightCm - minHeightCm + 1;
        String[] cmValues = new String[totalCm];
        for (int i=0; i<cmValues.length; i++) {
            cmValues[i] = (minHeightCm + i) + " cm";
        }

        // Set centimeter values for picker
        picker.setDisplayedValues(cmValues);

        // Add picker to layout
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.addView(picker, params);

        return linearLayout;
    }

    /* Define picker range */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        // Define picker range
        picker.setMinValue(minHeightCm);
        picker.setMaxValue(maxHeightCm);

        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(getValue());
    }

    /* Specify return value in inches */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();

            // If the value was changed set the value
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    /* Called when a Preference is being inflated and the default value attribute is read */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    /* Set the initial value of the Preference */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    /* Set the value of the Preference */
    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    /* Get the value of the Preference */
    public int getValue() {
        return this.value;
    }
}