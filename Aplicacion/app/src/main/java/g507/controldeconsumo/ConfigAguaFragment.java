package g507.controldeconsumo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.modelo.ServicioAgua;

public class ConfigAguaFragment extends Fragment implements TaskListener {

    private View view;
    private TextView txtK;
    private TextView txtZf;
    private TextView txtTgdf;
    private TextView txtSc;
    private TextView txtEf;
    private TextView txtSt;
    private TextView txtAud;
    private TextView txtFs;
    private TextView txtCl;
    private TextView txtFecUltFact;
    private Button btnGuardar;

    private ProgressDialog progressDialog;
    private boolean conectando = false;

    private static final DateFormat dateFormatVista = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat dateFormatGuardado = new SimpleDateFormat("yyyy-MM-dd");

    public ConfigAguaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Para que mantenga la instancia del fragment ante una recreacion del activity (rotacion)
        setRetainInstance(true);
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_config_agua, container, false);
        txtK = (TextView) view.findViewById(R.id.txtK);
        txtZf = (TextView) view.findViewById(R.id.txtZf);
        txtTgdf = (TextView) view.findViewById(R.id.txtTgdf);
        txtSc = (TextView) view.findViewById(R.id.txtSc);
        txtEf = (TextView) view.findViewById(R.id.txtEf);
        txtSt = (TextView) view.findViewById(R.id.txtSt);
        txtAud = (TextView) view.findViewById(R.id.txtAud);
        txtFs = (TextView) view.findViewById(R.id.txtFs);
        txtCl = (TextView) view.findViewById(R.id.txtCl);
        txtFecUltFact = (TextView) view.findViewById(R.id.txtFecUltFact);
        btnGuardar = (Button) view.findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar();
            }
        });

        cargarConfigLocal();

        return view;
    }

    /**
     * Completa los campos con los valores guardados localmente en caso que existan
     */
    private void cargarConfigLocal() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        double k = prefs.getFloat(getString(R.string.pref_agua_k), -1);
        double zf = prefs.getFloat(getString(R.string.pref_agua_zf), -1);
        double tgdf = prefs.getFloat(getString(R.string.pref_agua_tgdf), -1);
        double sc = prefs.getFloat(getString(R.string.pref_agua_sc), -1);
        double ef = prefs.getFloat(getString(R.string.pref_agua_ef), -1);
        double st = prefs.getFloat(getString(R.string.pref_agua_st), -1);
        double aud = prefs.getFloat(getString(R.string.pref_agua_aud), -1);
        int fs = prefs.getInt(getString(R.string.pref_agua_fs), -1);
        int cl = prefs.getInt(getString(R.string.pref_agua_cl), -1);
        String fecha = prefs.getString(getString(R.string.pref_agua_fecha_fact), "");

        if(k != -1){
            txtK.setText(String.valueOf(k));
        }
        if(zf != -1){
            txtZf.setText(String.valueOf(zf));
        }
        if(tgdf != -1){
            txtTgdf.setText(String.valueOf(tgdf));
        }
        if(sc != -1){
            txtSc.setText(String.valueOf(sc));
        }
        if(ef != -1){
            txtEf.setText(String.valueOf(ef));
        }
        if(st != -1){
            txtSt.setText(String.valueOf(st));
        }
        if(aud != -1){
            txtAud.setText(String.valueOf(aud));
        }
        if(fs != -1){
            txtFs.setText(String.valueOf(fs));
        }
        if(cl != -1){
            txtCl.setText(String.valueOf(cl));
        }
        if(fecha != ""){
            try {
                // Convierte del formato de fecha separado con "-" a "/"
                Date fechaGuardada = dateFormatGuardado.parse(fecha);
                txtFecUltFact.setText(String.valueOf(dateFormatVista.format(fechaGuardada)));
            } catch (ParseException e) {
                Log.d(this.getClass().getName(), "Error al parsear la fecha guardada");
            }
        }
    }

    private void guardar(){
        if(conectando){
            return;
        }

        double k = 0;
        double zf = 0;
        double tgdf = 0;
        double sc = 0;
        double ef = 0;
        double st = 0;
        double aud = 0;
        int fs = 0;
        int cl = 0;
        Timestamp fecUltFact = null;

        // Para control de errores
        boolean cancelar = false;
        View campoConError = null;

        // Reset mensajes de error
        txtK.setError(null);
        txtZf.setError(null);
        txtTgdf.setError(null);
        txtSc.setError(null);
        txtEf.setError(null);
        txtSt.setError(null);
        txtAud.setError(null);
        txtFs.setError(null);
        txtCl.setError(null);
        txtFecUltFact.setError(null);

        if(TextUtils.isEmpty(txtFecUltFact.getText())){
            txtFecUltFact.setError(getString(R.string.error_campo_requerido));
            campoConError = txtFecUltFact;
            cancelar = true;
        } else {
            try{
                Date dateUltFact = dateFormatVista.parse(txtFecUltFact.getText().toString());
                fecUltFact = new Timestamp(dateUltFact.getTime());
            } catch (ParseException e){
                txtFecUltFact.setError("Formato incorrecto");
                campoConError = txtFecUltFact;
                cancelar = true;
            }
        }

        if(TextUtils.isEmpty(txtCl.getText())){
            txtCl.setError(getString(R.string.error_campo_requerido));
            campoConError = txtCl;
            cancelar = true;
        } else {
            cl = Integer.parseInt(txtCl.getText().toString());
        }

        if(TextUtils.isEmpty(txtFs.getText())){
            txtFs.setError(getString(R.string.error_campo_requerido));
            campoConError = txtFs;
            cancelar = true;
        } else {
            fs = Integer.parseInt(txtFs.getText().toString());
        }

        if(TextUtils.isEmpty(txtAud.getText())){
            txtAud.setError(getString(R.string.error_campo_requerido));
            campoConError = txtAud;
            cancelar = true;
        } else {
            aud = Double.parseDouble(txtAud.getText().toString());
        }

        if(TextUtils.isEmpty(txtSt.getText())){
            txtSt.setError(getString(R.string.error_campo_requerido));
            campoConError = txtSt;
            cancelar = true;
        } else {
            st = Double.parseDouble(txtSt.getText().toString());
        }

        if(TextUtils.isEmpty(txtEf.getText())){
            txtEf.setError(getString(R.string.error_campo_requerido));
            campoConError = txtEf;
            cancelar = true;
        } else {
            ef = Double.parseDouble(txtEf.getText().toString());
        }

        if(TextUtils.isEmpty(txtSc.getText())){
            txtSc.setError(getString(R.string.error_campo_requerido));
            campoConError = txtSc;
            cancelar = true;
        } else {
            sc = Double.parseDouble(txtSc.getText().toString());
        }

        if(TextUtils.isEmpty(txtTgdf.getText())){
            txtTgdf.setError(getString(R.string.error_campo_requerido));
            campoConError = txtTgdf;
            cancelar = true;
        } else {
            tgdf = Double.parseDouble(txtTgdf.getText().toString());
        }

        if(TextUtils.isEmpty(txtZf.getText())){
            txtZf.setError(getString(R.string.error_campo_requerido));
            campoConError = txtZf;
            cancelar = true;
        } else {
            zf = Double.parseDouble(txtZf.getText().toString());
        }

        if(TextUtils.isEmpty(txtK.getText())){
            txtK.setError(getString(R.string.error_campo_requerido));
            campoConError = txtK;
            cancelar = true;
        } else {
            k = Double.parseDouble(txtK.getText().toString());
        }

        if(cancelar){
            campoConError.requestFocus();
        } else{
            ServicioAgua servicioAgua = new ServicioAgua(0, k, zf, tgdf, sc, ef, st, aud, fs, cl);
            servicioAgua.setFecFact(txtFecUltFact.getText().toString());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            guardarEnConfigLocal(prefs, servicioAgua);
            int idUsuario = prefs.getInt(getString(R.string.pref_sesion_inic), -1);

            if(Utils.conexionAInternetOk(getActivity())){
                new TaskRequestUrl(this).execute(ConstructorUrls.configAgua(idUsuario, servicioAgua, fecUltFact), "POST");
            } else{
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarEnConfigLocal(SharedPreferences prefs, ServicioAgua servicio){
        prefs.edit().putFloat(getString(R.string.pref_agua_k), (float) servicio.getK()).apply();
        prefs.edit().putFloat(getString(R.string.pref_agua_zf), (float) servicio.getZf()).apply();
        prefs.edit().putFloat(getString(R.string.pref_agua_tgdf), (float) servicio.getTgdf()).apply();
        prefs.edit().putFloat(getString(R.string.pref_agua_sc), (float) servicio.getSc()).apply();
        prefs.edit().putFloat(getString(R.string.pref_agua_ef), (float) servicio.getEf()).apply();
        prefs.edit().putFloat(getString(R.string.pref_agua_st), (float) servicio.getSt()).apply();
        prefs.edit().putFloat(getString(R.string.pref_agua_aud), (float) servicio.getAud()).apply();
        prefs.edit().putInt(getString(R.string.pref_agua_fs), servicio.getFs()).apply();
        prefs.edit().putInt(getString(R.string.pref_agua_cl), servicio.getCl()).apply();
        prefs.edit().putString(getString(R.string.pref_agua_fecha_fact), servicio.getFecFact()).apply();
    }

    @Override
    public void inicioRequest() {
        conectando = true;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {
        conectando = false;
        if(progressDialog != null){
            progressDialog.dismiss();
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
