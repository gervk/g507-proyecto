package g507.controldeconsumo;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigNotifFragment extends Fragment {

    private View view;
    private TextView txtVLimiteElec;
    private Switch switchNotifElec;
    private EditText txtLimiteElec;
    private TextView txtVLimiteAgua;
    private Switch switchNotifAgua;
    private EditText txtLimiteAgua;
    private Button btnGuardarNotif;

    public ConfigNotifFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_config_notif, container, false);
        txtVLimiteElec = (TextView) view.findViewById(R.id.txtVLimiteElec);
        switchNotifElec = (Switch) view.findViewById(R.id.swiNotifElec);
        txtLimiteElec = (EditText) view.findViewById(R.id.txtLimiteElec);
        txtVLimiteAgua = (TextView) view.findViewById(R.id.txtVLimiteAgua);
        switchNotifAgua = (Switch) view.findViewById(R.id.swiNotifAgua);
        txtLimiteAgua = (EditText) view.findViewById(R.id.txtLimiteAgua);
        btnGuardarNotif = (Button) view.findViewById(R.id.btnGuardarNotif);

        cargaConfig();

        switchNotifElec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                txtVLimiteElec.setEnabled(checked);
                txtLimiteElec.setEnabled(checked);
            }
        });
        switchNotifAgua.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                txtVLimiteAgua.setEnabled(checked);
                txtLimiteAgua.setEnabled(checked);
            }
        });

        btnGuardarNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar();
            }
        });

        return view;
    }

    /**
     * Carga valores y habilita/deshabilita campos segun config local
     */
    private void cargaConfig() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        boolean limitarElec = prefs.getBoolean(getString(R.string.pref_limitar_elect), false);
        txtVLimiteElec.setEnabled(limitarElec);
        txtLimiteElec.setEnabled(limitarElec);
        switchNotifElec.setChecked(limitarElec);
        if(limitarElec){
            int limiteElecGuardado = prefs.getInt(getString(R.string.pref_limite_elect), 0);
            txtLimiteElec.setText(String.valueOf(limiteElecGuardado));
        }

        boolean limitarAgua = prefs.getBoolean(getString(R.string.pref_limitar_agua), false);
        txtVLimiteAgua.setEnabled(limitarAgua);
        txtLimiteAgua.setEnabled(limitarAgua);
        switchNotifAgua.setChecked(limitarAgua);
        if(limitarAgua){
            int limiteAguaGuardado = prefs.getInt(getString(R.string.pref_limite_agua), 0);
            txtLimiteAgua.setText(String.valueOf(limiteAguaGuardado));
        }
    }

    /**
     * Validacion de campos, guardado en config local y BD
     */
    private void guardar() {
        boolean cancelar = false;
        View campoConError = null;

        txtLimiteElec.setError(null);
        txtLimiteAgua.setError(null);

        //Lectura de valores ingresados
        boolean notifElec = switchNotifElec.isChecked();
        boolean notifAgua = switchNotifAgua.isChecked();
        String limiteElec = txtLimiteElec.getText().toString();
        String limiteAgua = txtLimiteAgua.getText().toString();

        //Validacion de campos
        if(notifAgua){
            if(TextUtils.isEmpty(limiteAgua)){
                txtLimiteAgua.setError(getString(R.string.error_campo_requerido));
                campoConError = txtLimiteAgua;
                cancelar = true;
            }
        }

        if(notifElec){
            if(TextUtils.isEmpty(limiteElec)){
                txtLimiteElec.setError(getString(R.string.error_campo_requerido));
                campoConError = txtLimiteElec;
                cancelar = true;
            }
        }

        if(cancelar){
            campoConError.requestFocus();
        } else{
            //Guarda en config local
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.edit().putBoolean(getString(R.string.pref_limitar_elect), notifElec).apply();
            prefs.edit().putBoolean(getString(R.string.pref_limitar_agua), notifAgua).apply();
            if(notifElec){
                prefs.edit().putInt(getString(R.string.pref_limite_elect), Integer.valueOf(limiteElec)).apply();
            }
            if(notifAgua){
                prefs.edit().putInt(getString(R.string.pref_limite_agua), Integer.valueOf(limiteAgua)).apply();
            }

            //TODO guardar config notif en BD
            Toast.makeText(getActivity(), R.string.error_servidor_no_disp, Toast.LENGTH_SHORT).show();
        }
    }

}
