package g507.controldeconsumo;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.modelo.EmpresaElec;

public class ConfigElectFragment extends Fragment implements TaskListener{

    private View view;
    private Spinner spinEmpresa;
    private CalendarView datePickFechaFact;
    private Button btnGuardar;

    private boolean conectando;
    private ProgressDialog progressDialog;

    private static final DateFormat dateFormatGuardado = new SimpleDateFormat("yyyy-MM-dd");

    public ConfigElectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Si se esta volviendo de una rotacion de pantalla y sigue el request, muestra msj de espera
        if(conectando){
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_config_elec, container, false);
        spinEmpresa = (Spinner) view.findViewById(R.id.spinEmpresa);
        datePickFechaFact = (CalendarView) view.findViewById(R.id.datePickFechaFactElect);
        btnGuardar = (Button) view.findViewById(R.id.btnGuardar);

        //Opciones de empresas en los spinner
        ArrayAdapter<EmpresaElec> empresasElecAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, EmpresaElec.values());
        spinEmpresa.setAdapter(empresasElecAdapter);

        datePickFechaFact.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                GregorianCalendar calendar = new GregorianCalendar( year, month, dayOfMonth );
                datePickFechaFact.setDate(calendar.getTime().getTime(), true, true);
            }//met
        });
        datePickFechaFact.setMaxDate(new Date().getTime());

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
        int idEmpresa = prefs.getInt(getString(R.string.pref_empresa_elect), -1);
        String fecha = prefs.getString(getString(R.string.pref_fecha_fact_elect), "");

        EmpresaElec empresa = EmpresaElec.getEmpresaById(idEmpresa);
        if(empresa != null){
            for(int n = 0; n < spinEmpresa.getAdapter().getCount(); n++){
                if(empresa.equals(spinEmpresa.getAdapter().getItem(n))){
                    spinEmpresa.setSelection(n);
                    continue;
                }
            }
        }

        if(!fecha.equals("")){
            try {
                Date dateGuardado = dateFormatGuardado.parse(fecha);
                datePickFechaFact.setDate(dateGuardado.getTime(), true, true);
            } catch (ParseException e) {
                Log.d(this.getClass().getName(), "Error al parsear la fecha guardada");
            }
        }

    }

    private void guardar(){
        if(conectando){
            return;
        }

        int idEmpresaSelecc = ((EmpresaElec) spinEmpresa.getSelectedItem()).getId();
        //Date fechaSelecc = calendar.getTime();
        Date fechaSelecc = new Date(datePickFechaFact.getDate());
        Timestamp timeStampFechaSelecc = Utils.timestampServer(fechaSelecc);

        // Guarda en config local
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putInt(getString(R.string.pref_empresa_elect), idEmpresaSelecc).apply();
        prefs.edit().putString(getString(R.string.pref_fecha_fact_elect), dateFormatGuardado.format(fechaSelecc)).apply();

        int idUsuario = prefs.getInt(getString(R.string.pref_sesion_inic), -1);

        if(Utils.conexionAInternetOk(getActivity())){
            new TaskRequestUrl(this).execute(ConstructorUrls.configElec(idUsuario, idEmpresaSelecc, timeStampFechaSelecc), "POST");
        } else{
            Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void inicioRequest() {
        // Para que mantenga la instancia del fragment ante una recreacion del activity (rotacion)
        setRetainInstance(true);

        conectando = true;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {
        conectando = false;
        if(progressDialog != null){
            progressDialog.dismiss();

            setRetainInstance(false);
        }

        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    Toast.makeText(getActivity(), "Datos guardados" , Toast.LENGTH_SHORT).show();
                } else if(json.getString("status").equals("error")){
                    Toast.makeText(getActivity(), "Datos incorrectos" , Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), getString(R.string.error_traducc_datos) , Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_inesperado_serv) , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        // Cierra el progressDialog si se saca el fragment del activity (cuando se rota), sino tira excepcion
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDetach();
    }
}
