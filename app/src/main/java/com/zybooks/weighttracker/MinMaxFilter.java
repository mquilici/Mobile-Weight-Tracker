package com.zybooks.weighttracker;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Min/Max filter for editText dialogs
 */
public class MinMaxFilter implements InputFilter {

    private final float mFloatMin;
    private final float mFloatMax;
    private final int mLength;

    // Default constructor takes floats
    public MinMaxFilter(Float minValue, Float maxValue, Integer length) {
        this.mFloatMin = minValue;
        this.mFloatMax = maxValue;
        this.mLength = length;
    }

    // Overloaded constructor takes strings
    public MinMaxFilter(String minValue, String maxValue, String length) {
        this.mFloatMin = Float.parseFloat(minValue);
        this.mFloatMax = Float.parseFloat(maxValue);
        this.mLength = Integer.parseInt(length);
    }

    /* Define char sequence filter */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        // Parse string input to float
        float input = 0;
        try {
            input = Float.parseFloat(dest.toString() + source.toString());
        } catch (NumberFormatException e) {
            return null;
        }

        // Return empty strings if destination editText is too long
        if (dest.toString().length() > mLength)
            return "";

        // Return null if the input value is in the correct range
        if (isInRange(mFloatMin, mFloatMax, input))
            return null;

        return "";
    }

    private boolean isInRange(Float a, Float b, Float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}