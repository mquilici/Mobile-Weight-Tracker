package com.zybooks.weighttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddWeightFragment extends Fragment implements View.OnClickListener {

    private WeightDatabase mWeightDb;
    private EditText weightEditText, dateEditText;
    private TextView errorTextView, unitSystemTextView;
    private float weightValue;
    private long timeValue;
    private DatePickerDialog datePickerDialog;
    private Weight tableEntry;
    private boolean updateWeight;
    private float scaleFactor;
    private Button cancelButton;
    private Button okButton;
    private ConstraintLayout layout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Show back button
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Inflate layout
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        // Get weight database
        mWeightDb = WeightDatabase.getInstance(getActivity().getApplicationContext());

        // Get current username and weight unit from shared preferences
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Get unit system
        String unitSystem = mSharedPrefs.getString(SettingsFragment.PREFERENCE_UNIT_SYSTEM, getString(R.string.english));

        // Define weight scale factor based on weight unit
        float poundsPerKilogram = 2.20462f;
        scaleFactor = unitSystem.equals(getString(R.string.english)) ? 1f : 1/ poundsPerKilogram;
        String weightUnit = unitSystem.equals(getString(R.string.english)) ? "lb" : "kg";

        // Get views
        okButton = root.findViewById(R.id.addWeightOkButton);
        cancelButton = root.findViewById(R.id.addWeightCancelButton);
        weightEditText = root.findViewById(R.id.addWeightEditText);
        dateEditText = root.findViewById(R.id.addDateEditText);
        errorTextView = root.findViewById(R.id.addWeightErrorTextView);
        unitSystemTextView = root.findViewById(R.id.addWeightUnitTextView);
        layout = root.findViewById(R.id.add_fragment);

        // Set filter to limit entered values
        float minPossibleWeight = 0.00f;
        float maxPossibleWeight = 999.99f;
        int maxWeightDigits = 5;
        weightEditText.setFilters(new InputFilter[]{ new MinMaxFilter(minPossibleWeight, maxPossibleWeight, maxWeightDigits)});

        // Set listeners
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        layout.setOnClickListener(this);

        // Set weight unit in weight field
        unitSystemTextView.setText(weightUnit);

        // Get extras from WeightTableActivity
        //Intent intent = getActivity().getIntent();
        //Bundle extras = intent.getExtras();

        // If extras are not null (updating an existing weight)
        if (getArguments() != null) {

            // Set flag for updating weight
            updateWeight = true;

            // Get data from extras from WeightTableActivity
            //tableEntry = (Weight)extras.getSerializable("SELECTED_WEIGHT");
            tableEntry = (Weight)getArguments().getSerializable("SELECTED_WEIGHT");
            timeValue = tableEntry.getTime();
            weightValue = tableEntry.getWeight();

            // Convert timeValue to date string and put in date EditText
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timeValue);
            String date = DateFormat.format("MMM-dd", cal).toString();
            dateEditText.setText(date);
            dateEditText.setTag(timeValue); // Store full timestamp in date EditText's tag

            // Populate the weight EditText with the user-selected weight
            String weightString = String.format(Locale.US,"%.1f",(weightValue*scaleFactor));
            weightEditText.setText(weightString);

            // If extras are null (add a new weight)
        } else {
            // Set flag for not updating weight (adding weight)
            updateWeight = false;

            // No extras, populate date EditText with current date
            timeValue = System.currentTimeMillis();
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timeValue);
            String dateString = DateFormat.format("MMM-dd", cal).toString();
            dateEditText.setText(dateString);
            dateEditText.setTag(timeValue); // Store full timestamp in date EditText's tag

            // Populate the weight EditText with the most recent weight or 0 if none exists
            List<Weight> weights = mWeightDb.getWeights();
            float latestWeight;
            if (weights.size() > 0) {
                latestWeight = weights.get(0).getWeight();
            } else {
                latestWeight = 0f;
            }
            String weightString = String.format(Locale.US,"%.1f",(latestWeight*scaleFactor));
            weightEditText.setText(weightString);
            tableEntry = new Weight(timeValue, latestWeight);
        }

        // Define onClickListener for date EditText to launch DatePicker
        dateEditText.setOnClickListener(v1 -> {
            // Define calender class's instance and get current date, month, and year from calender
            final Calendar c = Calendar.getInstance();

            // Use full time in milliseconds from editText tag to set calendar date
            long tagTime = (long)dateEditText.getTag();
            c.setTimeInMillis(tagTime);

            // Split date into components
            int mYear = c.get(Calendar.YEAR);        // current year
            int mMonth = c.get(Calendar.MONTH);      // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            int mHour = c.get(Calendar.HOUR_OF_DAY); // current hour
            int mMinute = c.get(Calendar.MINUTE);    // current minute
            int mSecond = c.get(Calendar.SECOND);    // current second

            // Close keyboard if open from weight edit text
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
            dateEditText.clearFocus();

            // Define date picker dialog
            datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    String yearString = String.format(Locale.US,"%04d", year);
                    String monthString = String.format(Locale.US,"%02d", month + 1);
                    String dayString = String.format(Locale.US,"%02d", day);
                    String hourString = String.format(Locale.US,"%02d", mHour);
                    String minuteString = String.format(Locale.US,"%02d", mMinute);
                    String secondString = String.format(Locale.US,"%02d", mSecond);

                    // Create date string to set calendar
                    String dateStringFull = yearString + "-" + monthString + "-" + dayString + " " + hourString + ":" + minuteString + ":" + secondString;

                    // Convert date string to UTC timestamp
                    java.sql.Timestamp ts = java.sql.Timestamp.valueOf(dateStringFull);

                    // Get time in milliseconds from timestamp
                    timeValue = ts.getTime();

                    // Store time in dateEditText's tag
                    dateEditText.setTag(timeValue);

                    // Get formatted date string and apply to dateEditText
                    String date = DateFormat.format("MMM-dd", timeValue).toString();
                    dateEditText.setText(date);
                }
            }, mYear, mMonth, mDay);

            // Set maximum date to now plus a little otherwise user cannot select today
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+60*60*1000);

            // Show date picker dialog
            datePickerDialog.show();
        });
        
        return root;
    }

    /* Define actions for OK and CANCEL buttons */
    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.addWeightOkButton) {
            onOkAddWeightButtonClicked(view);
        } else if (id == R.id.addWeightCancelButton) {
            onCancelAddWeightButtonClicked(view);
        } else {
            // Clicking elsewhere on screen hides keyboard and clears focus
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            dateEditText.clearFocus();
        }
    }

    /* When OK button is pressed */
    public void onOkAddWeightButtonClicked(View view) {
        if (weightEditText.getText().toString().equals("") || weightEditText.getText().toString().equals(".")) {
            // No weight entered, display error
            errorTextView.setText(R.string.please_enter_a_weight);
        } else {
            // Parse weight value
            try {
                weightValue = Float.parseFloat(weightEditText.getText().toString()) / scaleFactor;
            } catch (Exception e) {
                weightValue = 0;
            }

            // Put entered values in extras returned to WeightTableActivity and then finish
            timeValue = (long) dateEditText.getTag();
            tableEntry.setWeight(weightValue);
            tableEntry.setTime(timeValue);

            // Update weight or add new weight
            if (updateWeight) {
                mWeightDb.updateWeight(tableEntry);
            } else {
                mWeightDb.addWeight(tableEntry);
            }

            // Close keyboard if open from weight edit text
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            dateEditText.clearFocus();

            getActivity().onBackPressed();
        }
    }

    /* When Cancel button is pressed finish */
    public void onCancelAddWeightButtonClicked(View view) {
        // Close keyboard if open from weight edit text
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        dateEditText.clearFocus();

        getActivity().onBackPressed();
    }

    /* Show keyboard whens starting */
    @Override
    public void onResume() {
        super.onResume();
        weightEditText.post(new Runnable() {
            @Override
            public void run() {
                weightEditText.requestFocus();
                InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imgr.showSoftInput(weightEditText, InputMethodManager.SHOW_IMPLICIT);
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