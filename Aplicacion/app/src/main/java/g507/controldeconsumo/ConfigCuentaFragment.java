package g507.controldeconsumo;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import g507.controldeconsumo.modelo.EmpresaAgua;
import g507.controldeconsumo.modelo.EmpresaElec;

public class ConfigCuentaFragment extends Fragment {

    private View view;
    private Switch swiMedirElec;
    private Spinner spinEmpresaElec;
    private Switch swiMedirAgua;
    private Spinner spinEmpresaAgua;
    private Button btnGuardar;

    public ConfigCuentaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_config_cuenta, container, false);
        swiMedirElec = (Switch) view.findViewById(R.id.swiMedirElec);
        spinEmpresaElec = (Spinner) view.findViewById(R.id.spinEmpresaElec);
        swiMedirAgua = (Switch) view.findViewById(R.id.swiMedirAgua);
        spinEmpresaAgua = (Spinner) view.findViewById(R.id.spinEmpresaAgua);
        btnGuardar = (Button) view.findViewById(R.id.btnGuardar);

        //Opciones de empresas en los spinner
        ArrayAdapter<EmpresaElec> empresasElecAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, EmpresaElec.values());
        spinEmpresaElec.setAdapter(empresasElecAdapter);
        ArrayAdapter<EmpresaAgua> empresasAguaAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, EmpresaAgua.values());
        spinEmpresaAgua.setAdapter(empresasAguaAdapter);

        //Listeners para la seleccion de medir o no un servicio
        swiMedirElec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                spinEmpresaElec.setEnabled(checked);
            }
        });
        swiMedirAgua.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                spinEmpresaAgua.setEnabled(true);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar();
            }
        });

        cargarConfig();

        return view;
    }

    private void cargarConfig(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        boolean medirElect = prefs.getBoolean(getString(R.string.pref_medir_elect), false);
        spinEmpresaElec.setEnabled(medirElect);
        if(medirElect){
            int idEmpresaElect = prefs.getInt(getString(R.string.pref_empresa_elect), 1);
            spinEmpresaElec.setSelection(idEmpresaElect - 1);
        }

        boolean medirAgua = prefs.getBoolean(getString(R.string.pref_medir_agua), false);
        spinEmpresaAgua.setEnabled(medirAgua);
        if(medirAgua){
            int idEmpresaAgua = prefs.getInt(getString(R.string.pref_empresa_agua), 1);
            spinEmpresaAgua.setSelection(idEmpresaAgua - 1);
        }

    }

    private void guardar(){
        boolean medirElect = swiMedirElec.isChecked();
        boolean medirAgua = swiMedirAgua.isChecked();
        EmpresaElec empresaElect = (EmpresaElec) spinEmpresaElec.getSelectedItem();
        EmpresaAgua empresaAgua = (EmpresaAgua) spinEmpresaAgua.getSelectedItem();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putBoolean(getString(R.string.pref_medir_elect), medirElect).apply();
        if(medirElect){
            prefs.edit().putInt(getString(R.string.pref_empresa_elect), empresaElect.getId()).apply();
        }
        prefs.edit().putBoolean(getString(R.string.pref_medir_agua), medirAgua).apply();
        if(medirAgua){
            prefs.edit().putInt(getString(R.string.pref_empresa_agua), empresaAgua.getId()).apply();
        }

        //TODO guardar config cuenta en bd
        Toast.makeText(getActivity(), R.string.error_servidor_no_disp, Toast.LENGTH_SHORT).show();
    }
}
