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
public class HeightPickerEnglishPreference extends DialogPreference {

    // Allowed range for user heights in inches
    public static final int maxHeightInches = 95;
    public static final int minHeightInches = 48;
    public static final int inchesPerFoot = 12;

    // Enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker picker1;
    private NumberPicker picker2;
    private int value;

    public HeightPickerEnglishPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightPickerEnglishPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* Define Height Pickers */
    @Override
    protected View onCreateDialogView() {

        // Define layout
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        picker1 = new NumberPicker(getContext());
        picker2 = new NumberPicker(getContext());
        picker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50, 50);
        layoutParams.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.weight = 1;

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.weight = 1;

        // Define foot values for picker
        int nFeet = (int)Math.ceil((maxHeightInches - minHeightInches) * 1f / inchesPerFoot) + 1;
        String[] footValues = new String[nFeet];
        for (int i=0; i<footValues.length; i++) {
            footValues[i] = ((minHeightInches / inchesPerFoot) + i) + "'";
        }

        // Define inch values for picker
        String[] inchValues = new String[inchesPerFoot];
        for (int i = 0; i < inchesPerFoot; i++) {
            inchValues[i] = i + "\"";
        }

        // Set foot and inch picker values
        picker1.setDisplayedValues(footValues);
        picker2.setDisplayedValues(inchValues);

        // Add pickers to view
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.addView(picker1, params1);
        linearLayout.addView(picker2, params2);

        return linearLayout;
    }

    /* Define picker ranges */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        // Define foot picker range
        picker1.setMinValue(minHeightInches / inchesPerFoot);
        picker1.setMaxValue(maxHeightInches / inchesPerFoot);
        picker1.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker1.setValue(getValue()/ inchesPerFoot);

        // Define inch picker range
        picker2.setMinValue(0);
        picker2.setMaxValue(inchesPerFoot -1);
        picker2.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker2.setValue(getValue()% inchesPerFoot);
    }

    /* Specify return value in inches */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker1.clearFocus();
            int newValue1 = picker1.getValue();
            int newValue2 = picker2.getValue();
            int finalValue = newValue1 * inchesPerFoot + newValue2;

            // If either value was changed set the value
            if (callChangeListener(newValue1) || callChangeListener(newValue2)) {
                setValue(finalValue);
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