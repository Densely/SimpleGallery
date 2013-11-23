package com.densely.simplegallery;



import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;



/**
 * com.new_amem.Activity
 *
 * @author: Алексей Дерендяев
 * Date: 27.03.13 10:01
 */
public class SettingsActivity extends PreferenceActivity {
    boolean fl = true;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences);
        //this.setContentView(R.xml.preferences);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        addPreferencesFromResource(R.xml.preferences);


        Preference preference = findPreference("langInterface");


        Log.d("SettingsActivity onCreate", "SettingsActivity onCreate");
        preference.setSummary(((ListPreference) preference).getEntry());


        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (preference.getKey().equals("langInterface") && fl) {
                    fl = false;
                    preference.setSummary(((ListPreference) preference).getEntry());
                    Log.d("SettingsActivity ", ((ListPreference) preference).getValue());
                    preference.getEditor().putString("langInterface", (String) newValue).commit();

                    System.exit(1);
                }
                return false;
            }
        });
    }
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);





        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("SettingsActivity onResume", "SettingsActivity onResume");

    }
}
