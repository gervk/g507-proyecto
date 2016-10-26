package g507.controldeconsumo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.Utils;

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

        return view;
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
        double fs = 0;
        double cl = 0;
        double fecUltFact;

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

        /*
        if(TextUtils.isEmpty(txtFecUltFact.getText())){
            txtFecUltFact.setError(getString(R.string.error_campo_requerido));
            campoConError = txtFecUltFact;
            cancelar = true;
        } else {

        }*/

        if(TextUtils.isEmpty(txtCl.getText())){
            txtCl.setError(getString(R.string.error_campo_requerido));
            campoConError = txtCl;
            cancelar = true;
        } else {
            cl = Double.parseDouble(txtCl.getText().toString());
        }

        if(TextUtils.isEmpty(txtFs.getText())){
            txtFs.setError(getString(R.string.error_campo_requerido));
            campoConError = txtFs;
            cancelar = true;
        } else {
            fs = Double.parseDouble(txtFs.getText().toString());
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
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int idUsuario = prefs.getInt(getString(R.string.pref_sesion_inic), -1);
            JSONObject jsonCoef = getJsonCoef(k, zf, tgdf, sc, ef, st, aud, fs, cl);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp timeStampHoy = Timestamp.valueOf(dateFormat.format(Calendar.getInstance().getTime()));

            if(Utils.conexionAInternetOk(getActivity())){
                new TaskRequestUrl(this).execute(ConstructorUrls.configAgua(idUsuario, jsonCoef, timeStampHoy), "POST");
            } else{
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private JSONObject getJsonCoef(double k, double zf, double tgdf, double sc, double ef, double st, double aud, double fs, double cl){
        JSONObject json = new JSONObject();

        try{
            json.put("k", k);
            json.put("zf", zf);
            json.put("tgdf", tgdf);
            json.put("sc", sc);
            json.put("ef", ef);
            json.put("st", st);
            json.put("aud", aud);
            json.put("fs", fs);
            json.put("cl", cl);
        } catch (JSONException e) {
            return null;
        }

        return json;
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
}
