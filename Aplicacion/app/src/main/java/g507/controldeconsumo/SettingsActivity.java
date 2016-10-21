package g507.controldeconsumo;

import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import g507.controldeconsumo.conexion.ConstructorUrls;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(!sharedPreferences.getBoolean(getString(R.string.pref_server_local), false)){
            // Si no se usa servidor local, setea url base como la de heroku
            ConstructorUrls.urlBase = ConstructorUrls.URL_BASE_CLOUD;
            Log.d("Settings", ConstructorUrls.urlBase);
        } else{
            // Si usa servidor local seta url base segun la ip configurada
            ConstructorUrls.urlBase = sharedPreferences.getString(getString(R.string.pref_ip_server), "");
            Log.d("Settings", ConstructorUrls.urlBase);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
