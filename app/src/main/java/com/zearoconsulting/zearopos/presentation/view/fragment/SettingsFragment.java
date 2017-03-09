package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.zearoconsulting.zearopos.R;

/**
 * Created by saravanan on 09-01-2017.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        /*
            public void addPreferencesFromResource (int preferencesResId)
                Inflates the given XML resource and adds the preference hierarchy to the
                current preference hierarchy.

            Parameters
                preferencesResId : The XML resource ID to inflate.
        */
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pos_settings);

        Preference pref = findPreference( "developer" );
        pref.setSummary("Zearo Apps");

        Preference pref1 = findPreference( "version" );
        try {
            pref1.setSummary(appVersion());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        String version = pInfo.versionName;
        return version;
    }
}
