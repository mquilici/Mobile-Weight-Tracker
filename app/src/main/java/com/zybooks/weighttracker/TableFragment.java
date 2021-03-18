package com.zybooks.weighttracker;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.view.ActionMode;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class TableFragment extends Fragment implements View.OnClickListener {

    private WeightDatabase mWeightDb;
    private WeightAdapter mWeightAdapter;
    private RecyclerView mRecyclerView;
    private Weight mSelectedWeight;
    private int mSelectedWeightPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;
    private SharedPreferences mSharedPrefs;
    private String unitSystem;
    private String goalType;
    private float goalWeight;
    private float scaleFactor;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_table, container, false);

        // Define shared preferences
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        // Change dark mode state
        boolean mDarkTheme = mSharedPrefs.getBoolean(SettingsFragment.PREFERENCE_DARK_MODE, false);
        if (mDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Define floating action button
        fab = root.findViewById(R.id.weightTableAddButton);
        fab.setOnClickListener(this);

        // Instantiate weight database
        mWeightDb = WeightDatabase.getInstance(getActivity().getApplicationContext());

        // Get current username from shared preferences
        unitSystem = mSharedPrefs.getString(SettingsFragment.PREFERENCE_UNIT_SYSTEM, getString(R.string.english));
        goalType = mSharedPrefs.getString("goal_type","l");
        goalWeight = mSharedPrefs.getFloat("goal_weight",0);

        // Define weight scale factor based on weight unit
        float poundsPerKilogram = 2.20462f;
        scaleFactor = unitSystem.equals(getString(R.string.english)) ? 1f : 1 / poundsPerKilogram;

        // Lookup the recyclerview in activity layout
        mRecyclerView = root.findViewById(R.id.weightRecyclerView);

        // Create adapter passing in the sample user data
        mWeightAdapter = new WeightAdapter(loadWeights());

        // Create alternating light and dark row backgrounds using decorations
        mRecyclerView.addItemDecoration(new BackgroundItemDecoration(R.drawable.row_light, R.drawable.row_dark));

        // Attach the adapter to the recyclerview to populate items
        mRecyclerView.setAdapter(mWeightAdapter);

        // Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        return root;
    }

    /* Define click actions */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // If the addWeight FAB is clicked
            case R.id.weightTableAddButton:
                if (mSelectedWeightPosition != RecyclerView.NO_POSITION) {
                    mActionMode.finish();
                }
                Navigation.findNavController(getView()).navigate(R.id.navigation_add_weight);
                break;
            default:
                if (mActionMode != null) {
                    mActionMode.finish();
                }
                break;
        }
    }

    /* Method to get weights from database to populate recycler view */
    private List<Weight> loadWeights() {
        return mWeightDb.getWeights();
    }

    /* Class that binds data to recycler view */
    private class WeightHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Weight mWeight;
        private final TextView mWeightText;
        private final TextView mTimeText;
        private final TextView mDeltaGoalText;
        private final TextView mDeltaGoalHeader;
        private final TextView mBmiText;
        private final TextView mBmiHeader;

        public WeightHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            mWeightText = itemView.findViewById(R.id.weightTextView);
            mTimeText = itemView.findViewById(R.id.timeTextView);
            mDeltaGoalText = itemView.findViewById(R.id.deltaTextView);
            mDeltaGoalHeader = getActivity().findViewById(R.id.deltaHeader);
            mBmiText = itemView.findViewById(R.id.bmiTextView);
            mBmiHeader = getActivity().findViewById(R.id.bmiHeader);
        }

        public void bind(Weight weight, int position) {
            mWeight = weight;

            // Get current date
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(weight.getTime());
            String date = DateFormat.format("MMM-dd", cal).toString();

            // Convert weight
            String weightUnit = unitSystem.equals(getString(R.string.english)) ? "lb" : "kg";
            String weightString = String.format(Locale.US, "%.1f", (weight.getWeight() * scaleFactor)) + " " + weightUnit;

            // Get height from shared preferences
            int heightInInches = mSharedPrefs.getInt(SettingsFragment.PREFERENCE_HEIGHT_ENGLISH, 0);

            // Get weight from editText
            float weightInPounds = mWeight.getWeight();

            // Compute BMI
            float bmi = calculateBmi(weightInPounds, heightInInches);

            // Add date and weight values to row
            mTimeText.setText(date);
            mWeightText.setText(weightString);

            // Compute difference between weight and goal weight
            float deltaGoalWeight = weight.getWeight() - goalWeight;

            // If a goal weight is set and goal column setting is on, display goal column
            if (goalWeight > 0 && mSharedPrefs.getBoolean(SettingsFragment.PREFERENCE_GOAL_COLUMN, false)) {
                mDeltaGoalHeader.setVisibility(View.VISIBLE);
                mDeltaGoalText.setVisibility(View.VISIBLE);
                String deltaString = String.format(Locale.US, "%+.1f", (deltaGoalWeight * scaleFactor)) + " " + weightUnit;
                mDeltaGoalText.setText(deltaString);
            } else {
                // Hide goal column
                mDeltaGoalText.setText("");
                mDeltaGoalHeader.setVisibility(View.GONE);
                mDeltaGoalText.setVisibility(View.GONE);
            }

            // If a height is set and BMI column setting is on, display BMI column
            if (heightInInches > 0 && mSharedPrefs.getBoolean(SettingsFragment.PREFERENCE_BMI_COLUMN, false)) {
                mBmiHeader.setVisibility(View.VISIBLE);
                mBmiText.setVisibility(View.VISIBLE);
                String bmiString = String.format(Locale.US, "%.1f", (bmi));
                mBmiText.setText(bmiString);
            } else {
                // Hide BMI column
                mBmiText.setText("");
                mBmiHeader.setVisibility(View.GONE);
                mBmiText.setVisibility(View.GONE);
            }

            // Set listener when user clicks on a row
            itemView.setOnClickListener(this);

            // Change row color when user clicks on a row
            if (mSelectedWeightPosition == position) {
                // Change view background color
                mTimeText.setBackgroundColor(getResources().getColor(R.color.colorTableSelected));
                mWeightText.setBackgroundColor(getResources().getColor(R.color.colorTableSelected));
                mDeltaGoalText.setBackgroundColor(getResources().getColor(R.color.colorTableSelected));
                mBmiText.setBackgroundColor(getResources().getColor(R.color.colorTableSelected));

                // Invert view text color when highlighted
                mTimeText.setTextColor(getResources().getColor(R.color.colorPrimary));
                mWeightText.setTextColor(getResources().getColor(R.color.colorPrimary));
                mDeltaGoalText.setTextColor(getResources().getColor(R.color.colorPrimary));
                mBmiText.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                // Change view background back to transparent
                mTimeText.setBackgroundColor(Color.TRANSPARENT);
                mWeightText.setBackgroundColor(Color.TRANSPARENT);
                mDeltaGoalText.setBackgroundColor(Color.TRANSPARENT);
                mBmiText.setBackgroundColor(Color.TRANSPARENT);

                // Make view text normal color
                mTimeText.setTextColor(getResources().getColor(R.color.colorDarkText));
                mWeightText.setTextColor(getResources().getColor(R.color.colorDarkText));
                mBmiText.setTextColor(getResources().getColor(R.color.colorDarkText));

                // Make weight goal deltas red if user achieves goal otherwise make them green
                if ((goalType.equals("l") && deltaGoalWeight > 0) || (goalType.equals("g") && deltaGoalWeight < 0)) {
                    mDeltaGoalText.setTextColor(getResources().getColor(R.color.colorDarkText));
                } else {
                    mDeltaGoalText.setTextColor(getResources().getColor(R.color.green));
                }
            }
        }

        // Compute BMI
        private float calculateBmi(float weightInPounds, int heightInInches) {
            float bmiConstant = 703f;
            if (heightInInches > 0) {
                return (bmiConstant * weightInPounds) / heightInInches / heightInInches;
            } else {
                return 0f;
            }
        }

        /* Highlight selected row */
        @Override
        public void onClick(View view) {
            if (mSelectedWeightPosition == RecyclerView.NO_POSITION) {

                if (mActionMode != null) {
                    return;
                }

                // Selected weight
                mSelectedWeight = mWeight;

                // Get the position of the selected item
                mSelectedWeightPosition = getAdapterPosition();

                // Inform the Adapter that the item at the given position has changed
                mWeightAdapter.notifyItemChanged(mSelectedWeightPosition);

                // Show the CAB
                mActionMode = getActivity().startActionMode(mActionModeCallback);

            } else {

                mActionMode.finish();

            }
        }
    }

    /* Manages the list of subjects displayed in the RecyclerView */
    private class WeightAdapter extends RecyclerView.Adapter<WeightHolder> {

        private final List<Weight> mWeightList;

        public WeightAdapter(List<Weight> weights) {
            mWeightList = weights;
        }

        @NonNull
        @Override
        public WeightHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity().getApplicationContext());
            return new WeightHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(WeightHolder holder, int position) {
            holder.bind(mWeightList.get(position), position);
        }

        /* Get number of weights in table */
        @Override
        public int getItemCount() {
            return mWeightList.size();
        }

        /* Add weight to table */
        public void addWeight(Weight newWeight) {
            int index = 0;

            // Find weight index based on time (table is sorted by time)
            for (Weight rowWeight : mWeightList) {
                if (newWeight.getTime() >= rowWeight.getTime()) {
                    break;
                }
                index += 1;
            }

            // Add weight to table at specified index
            mWeightList.add(index, newWeight);

            // Notify that the weight was inserted
            notifyItemInserted(index);

            // Scroll to top of list to see weight
            if (index == 0) {
                mRecyclerView.scrollToPosition(index);
            }
        }

        /* Remove weight from table */
        public void removeWeight(Weight weight) {
            // Find index of weight
            int index = mWeightList.indexOf(weight);

            // If the index exists, remove item
            if (index >= 0) {
                mWeightList.remove(index);
                notifyItemRemoved(index);
            }
        }

        /* Update a weight in the table by finding id, removing old weight, then adding new weight */
//        public void updateWeight(Weight newWeight) {
//            for (Weight rowWeight : mWeightList) {
//
//                if (newWeight.getId() == rowWeight.getId()) {
//                    removeWeight(rowWeight);
//                    addWeight(newWeight);
//                    notifyItemChanged(mWeightList.indexOf(rowWeight));
//                    break;
//                }
//            }
//        }
    }

    /* Manages the lifecycle of the action mode and displays CAB */
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Provide context menu for CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /* Define action bar click action */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            // Process action item selection
            switch (item.getItemId()) {

                // If delete icon is clicked
                case R.id.delete:

                    // Delete weight from the database
                    mWeightDb.deleteWeight(mSelectedWeight.getId());

                    // Remove item in RecyclerView
                    mWeightAdapter.removeWeight(mSelectedWeight);

                    // Close the CAB
                    mode.finish();

                    // Define Undo snackbar
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.weightRecyclerView), R.string.weight_deleted, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.undo, new View.OnClickListener() {
                        /* Define on click listener */
                        @Override
                        public void onClick(View v) {
                            // Add weight back into database
                            mWeightDb.addWeight(mSelectedWeight);      // Add weight to database
                            mWeightAdapter.addWeight(mSelectedWeight); // Add item to list
                        }
                    });

                    // Show undo snackbar after deleting an entry
                    snackbar.show();
                    return true;

                // If edit icon is clicked
                case R.id.edit:

                    // Add selected weight to bundle
                    Bundle extras = new Bundle();
                    extras.putSerializable("SELECTED_WEIGHT",mSelectedWeight);

                    // Open add weight screen
                    Navigation.findNavController(getView()).navigate(R.id.navigation_add_weight, extras);

                    // Close action bar
                    mode.finish();
                    return true;

                default:

                    // Close action bar
                    mode.finish();
                    return false;
            }
        }

        // Close action bar
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;

            // CAB closing, need to deselect item if not deleted
            mWeightAdapter.notifyItemChanged(mSelectedWeightPosition);
            mSelectedWeightPosition = RecyclerView.NO_POSITION;
        }
    };

    /* Create alternating light and dark table row backgrounds */
    private static class BackgroundItemDecoration extends RecyclerView.ItemDecoration {

        private final int mOddBackground;
        private final int mEvenBackground;

        // Constructor takes two drawables to use for light and dark backgrounds
        public BackgroundItemDecoration(@DrawableRes int oddBackground, @DrawableRes int evenBackground) {
            mOddBackground = oddBackground;
            mEvenBackground = evenBackground;
        }

        // Set view background to the different drawables depending if position is odd or even
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            boolean isEvenRow = position % 2 == 0;
            view.setBackgroundResource(isEvenRow ? mEvenBackground : mOddBackground);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mActionMode!=null)
        mActionMode.finish();
    }
}