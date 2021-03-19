package com.zybooks.weighttracker;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;

public class GoalFragment extends Fragment implements View.OnClickListener {

    private EditText goalWeightEditText;
    private TextView goalErrorTextView;
    private RadioGroup goalWeightTypeRadio;
    private SharedPreferences mSharedPrefs;
    private RadioButton goalWeightLossRadio;
    private RadioButton goalWeightGainRadio;
    private TextView goalUnitTextView;
    private Button okButton, cancelButton;
    private float scaleFactor;
    private float goalWeight;
    private ConstraintLayout layout;

    /* Define Height Pickers */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_goal, container, false);

        // Get current username and weight unit from shared preferences
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitSystem = mSharedPrefs.getString(SettingsFragment.PREFERENCE_UNIT_SYSTEM, "English");

        // Define weight scale factor based on weight unit
        float poundsPerKilogram = 2.20462f;
        scaleFactor = unitSystem.equals(getString(R.string.english)) ? 1f : 1/ poundsPerKilogram;

        // Get views
        goalWeightEditText = root.findViewById(R.id.goalWeightEditText);
        goalErrorTextView = root.findViewById(R.id.goalWeightErrorTextView);
        goalWeightTypeRadio = root.findViewById(R.id.goalWeightRadioGroup);
        goalWeightLossRadio = root.findViewById(R.id.goalWeightLossRadioButton);
        goalWeightGainRadio = root.findViewById(R.id.goalWeightGainRadioButton);
        goalUnitTextView = root.findViewById(R.id.goalWeightUnitTextView);
        okButton = root.findViewById(R.id.goalWeightOkButton);
        cancelButton = root.findViewById(R.id.goalWeightCancelButton);
        layout = root.findViewById(R.id.goal_fragment);
        goalWeightEditText.setFilters(new InputFilter[]{ new MinMaxFilter(0.00f, 999.99f,5)});

        // Set onclick listeners
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        layout.setOnClickListener(this);

        // Set unit for goal weight EditText
        String weightUnit = unitSystem.equals("English") ? "lb" : "kg";

        goalUnitTextView.setText(weightUnit);

        // Set goal weight to user's stored goal weight on creation
        float goalWeight = mSharedPrefs.getFloat("goal_weight",0);
        String goalType = mSharedPrefs.getString("goal_type","l");

        // Convert current goal weight and put in goal EditText
        String goalString = String.format(Locale.US,"%.1f",(goalWeight*scaleFactor));
        goalWeightEditText.setText(goalString);

        // Set radio button to user's stored goal type preferences
        if (goalType.equals("g")) {
            goalWeightLossRadio.setChecked(false);
            goalWeightGainRadio.setChecked(true);
        } else {
            goalWeightLossRadio.setChecked(true);
            goalWeightGainRadio.setChecked(false);
        }

        return root;
    }

    /* Define action for OK and CANCEL button */
    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.goalWeightOkButton) {
            onOkGoalWeightButtonClicked(view);
        } else if (id == R.id.goalWeightCancelButton) {
            onCancelGoalWeightButtonClicked(view);
        } else {
            // Clicking elsewhere on screen hides keyboard and clears focus
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            goalWeightEditText.clearFocus();
        }
    }

    /* Method to handle OK button click */
    public void onOkGoalWeightButtonClicked(View view) {
        int checkedId = goalWeightTypeRadio.getCheckedRadioButtonId();

        // Check if goal weight has been entered then store values in prefs
        if (goalWeightEditText.getText().toString().equals("") || goalWeightEditText.getText().toString().equals(".")) {
            goalErrorTextView.setText(R.string.please_enter_a_goal_weight);
        } else {
            // Parse goal weight
            try {
                goalWeight = Float.parseFloat(goalWeightEditText.getText().toString()) / scaleFactor;
            } catch (Exception e) {
                goalWeight = 0;
            }

            // Get goal type (weight loss or weight gain) from radio buttons
            String goalType;
            if (checkedId == R.id.goalWeightLossRadioButton) {
                goalType = "l";
            } else {
                goalType = "g";
            }

            // Update goal weight in preferences
            SharedPreferences.Editor editor = mSharedPrefs.edit();
            editor.putFloat("goal_weight", goalWeight);
            editor.putString("goal_type", goalType);
            editor.putBoolean("goal_column", true);
            editor.apply();

            // Close keyboard if open from weight edit text
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            goalWeightEditText.clearFocus();

            getActivity().onBackPressed();

        }
    }

    /* Method to handle cancel button click */
    public void onCancelGoalWeightButtonClicked(View view) {
        // Close keyboard if open from weight edit text
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        goalWeightEditText.clearFocus();

        getActivity().onBackPressed();
    }

    /* Show keyboard when starting */
    @Override
    public void onResume() {
        super.onResume();
        goalWeightEditText.post(new Runnable() {
            @Override
            public void run() {
                goalWeightEditText.requestFocus();
                InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imgr.showSoftInput(goalWeightEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    /* Hide keyboard when leaving */
    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}