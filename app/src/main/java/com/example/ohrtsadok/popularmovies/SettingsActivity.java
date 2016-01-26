package com.example.ohrtsadok.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;


/**
 * Created by ohrtsadok on 1/26/16.
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

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
        public void onCreatePreferences(Bundle bundle, String s) {

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
