package com.zybooks.weighttracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        // Set result
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
    }

    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }

}