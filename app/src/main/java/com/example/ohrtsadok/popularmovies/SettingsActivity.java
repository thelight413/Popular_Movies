/*
 * Copyright (C) 2016 The Android Open Source Project
 * Created by ohrtsadok on 2/5/16.
 */
package com.example.ohrtsadok.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;


public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        public SettingsFragment() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            ListPreference listPreference = (ListPreference) findPreference(getString(R.string.sortbyKey));
            String preferenceValue = listPreference.getValue();
            int index = listPreference.findIndexOfValue(preferenceValue);
            listPreference.setSummary(listPreference.getEntries()[index]);
            listPreference.setOnPreferenceChangeListener(this);


        }



        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = String.valueOf(newValue);
            int index = ((ListPreference) preference).findIndexOfValue(stringValue);
            preference.setSummary(((ListPreference) preference).getEntries()[index]);


            return true;
        }

    }

}
