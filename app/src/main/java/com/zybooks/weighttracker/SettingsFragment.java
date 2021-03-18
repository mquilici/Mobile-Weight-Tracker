package com.zybooks.weighttracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREFERENCE_DARK_MODE = "night_mode";
    public static final String PREFERENCE_UNIT_SYSTEM = "weight_unit";
    public static final String PREFERENCE_CLEAR_WEIGHTS = "pref_clear";
    public static final String PREFERENCE_HEIGHT_ENGLISH = "pref_height";
    public static final String PREFERENCE_HEIGHT_METRIC = "pref_height_metric";
    public static final String PREFERENCE_USER_CATEGORY = "user_category";
    public static final String PREFERENCE_BMI_COLUMN = "bmi_column";
    public static final String PREFERENCE_GOAL_COLUMN = "goal_column";

    private PreferenceCategory mCategory;
    private HeightPickerMetricPreference mMetricPref;
    private HeightPickerEnglishPreference mEnglishPref;
    private final int inchesPerFoot = 12;
    private final float cmPerInch = 2.54f;
    private final int defaultHeightInches = 70;
    private final int defaultHeightCm = 178;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Access the default shared prefs
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Set preferences
        setPrefUnit(sharedPreferences);

        // Set height picker preferences
        setPrefHeightEnglish(sharedPreferences);
        setPrefHeightMetric(sharedPreferences);


        mCategory = (PreferenceCategory) findPreference(PREFERENCE_USER_CATEGORY);

        // Load height picker preferences
        mMetricPref = (HeightPickerMetricPreference) findPreference(PREFERENCE_HEIGHT_METRIC);
        mEnglishPref = (HeightPickerEnglishPreference) findPreference(PREFERENCE_HEIGHT_ENGLISH);

        // Add either english or metric height picker depending on unit preferences
        if (sharedPreferences.getString(PREFERENCE_UNIT_SYSTEM,getString(R.string.english)).equals(getString(R.string.english))) {
            mCategory.removePreference(mMetricPref);
            mCategory.addPreference(mEnglishPref);
        } else {
            mCategory.removePreference(mEnglishPref);
            mCategory.addPreference(mMetricPref);
        }

        // Create listener for clearing weights
        Preference clearPreference = findPreference(PREFERENCE_CLEAR_WEIGHTS);
        clearPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CreateMessage("Clear all weights?", sharedPreferences);
                return true;
            }
        });
    }

    /* Set unit preference string value and summary */
    private void setPrefUnit(SharedPreferences sharedPrefs) {
        Preference unitPref = findPreference(PREFERENCE_UNIT_SYSTEM);
        String unit = sharedPrefs.getString(PREFERENCE_UNIT_SYSTEM,getString(R.string.english));
        unitPref.setSummary(unit);
    }

    /* Set english height value and summary */
    private void setPrefHeightEnglish(SharedPreferences sharedPrefs) {
        // Get height from shared preferences
        Preference heightPref = findPreference(PREFERENCE_HEIGHT_ENGLISH);
        int height = sharedPrefs.getInt(SettingsFragment.PREFERENCE_HEIGHT_ENGLISH,defaultHeightInches);

        // Determine feet and inches
        int feet = height/inchesPerFoot;
        int inches = height%inchesPerFoot;

        // Set summary string
        heightPref.setSummary(feet + "' " + inches + "\"" );
    }

    /* Set metric height value and summary */
    private void setPrefHeightMetric(SharedPreferences sharedPrefs) {
        Preference heightPref = findPreference(PREFERENCE_HEIGHT_METRIC);
        int height = sharedPrefs.getInt(SettingsFragment.PREFERENCE_HEIGHT_METRIC,defaultHeightCm);
        heightPref.setSummary(height + " " + getResources().getString(R.string.cm));
    }

    /* When the activity enters the Resumed state */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /* When the activity enters the Paused state */
    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /* Method to specify actions when preferences are changed */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int height, convertedHeight;

        switch (key) {

            // Change to dark mode when dark mode switch is pressed
            case PREFERENCE_DARK_MODE:
                // Change dark mode state
                boolean mDarkTheme = sharedPreferences.getBoolean(SettingsFragment.PREFERENCE_DARK_MODE, false);
                if (mDarkTheme) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;

            // Store unit system (English or Metric)
            case PREFERENCE_UNIT_SYSTEM:
                setPrefUnit(sharedPreferences);

                if (sharedPreferences.getString(PREFERENCE_UNIT_SYSTEM,getString(R.string.english)).equals(getString(R.string.english))) {
                    mCategory.addPreference(mEnglishPref);
                    mCategory.removePreference(mMetricPref);
                } else {
                    mCategory.addPreference(mMetricPref);
                    mCategory.removePreference(mEnglishPref);
                }
                break;

            // store english height value
            case PREFERENCE_HEIGHT_ENGLISH:
                Preference heightPrefEnglish = findPreference(PREFERENCE_HEIGHT_ENGLISH);
                height = sharedPreferences.getInt(SettingsFragment.PREFERENCE_HEIGHT_ENGLISH,defaultHeightInches);
                heightPrefEnglish.setSummary(height/inchesPerFoot + "' " + height%inchesPerFoot + "\"");

                // Convert height to cm
                convertedHeight = Math.round(height * cmPerInch);

                // Store height in cm
                onPause();
                sharedPreferences.edit().putInt(PREFERENCE_HEIGHT_METRIC,convertedHeight).apply();
                onResume();

                // Create string summary
                mMetricPref.setSummary(convertedHeight + " " + getResources().getString(R.string.cm));
                break;

            // Store metric height value
            case PREFERENCE_HEIGHT_METRIC:
                Preference heightPrefMetric = findPreference(PREFERENCE_HEIGHT_METRIC);
                height = sharedPreferences.getInt(SettingsFragment.PREFERENCE_HEIGHT_METRIC,defaultHeightCm);
                heightPrefMetric.setSummary(height + " " + getResources().getString(R.string.cm));

                // Convert height to inches
                convertedHeight = (int) Math.round(height / cmPerInch);

                // Store height in inches
                onPause();
                sharedPreferences.edit().putInt(PREFERENCE_HEIGHT_ENGLISH,convertedHeight).apply();
                onResume();

                // Determine feet and inches
                int feet = convertedHeight/inchesPerFoot;
                int inches = convertedHeight%inchesPerFoot;

                // Create string summary
                mEnglishPref.setSummary(feet + "' " + inches + "\"" );
                break;
        }
    }

    /* Create an alert when the user wants to clear weights */
    private void CreateMessage(String message, SharedPreferences sharedPrefs){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);

        // Define OK button
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                WeightDatabase mWeightDb = WeightDatabase.getInstance(getActivity());
                mWeightDb.clearWeights();
            }
        });

        // Define CANCEL button
        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        // Create alert
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}