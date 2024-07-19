package es.unex.infinitetime.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import es.unex.infinitetime.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}