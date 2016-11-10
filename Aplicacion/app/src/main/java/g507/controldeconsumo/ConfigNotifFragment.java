package g507.controldeconsumo;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.modelo.TipoConsumo;

public class ConfigNotifFragment extends Fragment implements TaskListener{
    // Ids para los controles de consumo en el alarmManager
    private static final int CODIGO_CONTROL_ELEC = 500;
    private static final int CODIGO_CONTROL_AGUA = 501;

    private View view;
    private TextView txtVLimiteElec;
    private Switch switchNotifElec;
    private EditText txtLimiteElec;
    private TextView txtVLimiteAgua;
    private Switch switchNotifAgua;
    private EditText txtLimiteAgua;
    private Button btnGuardarNotif;

    private int limitesActualizados = 0;
    private boolean conectando = false;
    private ProgressDialog progressDialog;

    public ConfigNotifFragment() {
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

        int limiteElecGuardado = prefs.getInt(getString(R.string.pref_limite_elect), -1);
        boolean limitarElec = limiteElecGuardado != -1;
        txtVLimiteElec.setEnabled(limitarElec);
        txtLimiteElec.setEnabled(limitarElec);
        switchNotifElec.setChecked(limitarElec);
        if(limitarElec){
            txtLimiteElec.setText(String.valueOf(limiteElecGuardado));
        }

        int limiteAguaGuardado = prefs.getInt(getString(R.string.pref_limite_agua), -1);
        boolean limitarAgua = limiteAguaGuardado != -1;
        txtVLimiteAgua.setEnabled(limitarAgua);
        txtLimiteAgua.setEnabled(limitarAgua);
        switchNotifAgua.setChecked(limitarAgua);
        if(limitarAgua){
            txtLimiteAgua.setText(String.valueOf(limiteAguaGuardado));
        }
    }

    /**
     * Validacion de campos, guardado en config local y BD y config del alarmManager para control
     */
    private void guardar() {
        // -1 como valor por defecto por si no limita
        int limiteElec = -1;
        int limiteAgua = -1;

        boolean cancelar = false;
        View campoConError = null;

        txtLimiteElec.setError(null);
        txtLimiteAgua.setError(null);

        boolean notifElec = switchNotifElec.isChecked();
        boolean notifAgua = switchNotifAgua.isChecked();

        // Validacion/lectura de campos
        if(notifAgua){
            if(TextUtils.isEmpty(txtLimiteAgua.getText())){
                txtLimiteAgua.setError(getString(R.string.error_campo_requerido));
                campoConError = txtLimiteAgua;
                cancelar = true;
            } else {
                try{
                    limiteAgua = Integer.parseInt(txtLimiteAgua.getText().toString());
                } catch (NumberFormatException e) {
                    txtLimiteAgua.setError(getString(R.string.error_campo_formato));
                    campoConError = txtLimiteAgua;
                    cancelar = true;
                }
            }
        }

        if(notifElec){
            if(TextUtils.isEmpty(txtLimiteElec.getText())){
                txtLimiteElec.setError(getString(R.string.error_campo_requerido));
                campoConError = txtLimiteElec;
                cancelar = true;
            } else {
                try {
                    limiteElec = Integer.parseInt(txtLimiteElec.getText().toString());
                } catch (NumberFormatException e) {
                    txtLimiteElec.setError(getString(R.string.error_campo_formato));
                    campoConError = txtLimiteElec;
                    cancelar = true;
                }
            }
        }

        if(cancelar){
            campoConError.requestFocus();
        } else{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int codArduino = prefs.getInt(getString(R.string.pref_id_arduino), -1);

            if(codArduino != -1){
                // Guarda en config local
                prefs.edit().putInt(getString(R.string.pref_limite_elect), limiteElec).apply();
                prefs.edit().putInt(getString(R.string.pref_limite_agua), limiteAgua).apply();

                if(Utils.conexionAInternetOk(getActivity())){
                    // Guarda en BD
                    int idUsuario = prefs.getInt(getString(R.string.pref_sesion_inic), -1);
                    conectando = true;
                    new TaskRequestUrl(this).execute(ConstructorUrls.guardarLimite(idUsuario,
                            TipoConsumo.ELECTRICIDAD, limiteElec), "POST");
                    new TaskRequestUrl(this).execute(ConstructorUrls.guardarLimite(idUsuario,
                            TipoConsumo.AGUA, limiteAgua), "POST");
                } else {
                    Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
                }
                // Config control de sobrepaso de los limites
                setControl(getActivity(), TipoConsumo.ELECTRICIDAD, limiteElec, codArduino);
                setControl(getActivity(), TipoConsumo.AGUA, limiteAgua, codArduino);
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_no_arduino), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Configura el alarmManager para que cada cierto tiempo controle el consumo acumulado del mes
     * @param limite si es -1 solo cancela la alarma anterior en caso que hubiera una
     */
    public static void setControl(Activity activity, TipoConsumo tipoConsumo, Integer limite, Integer codArduino) {
        PendingIntent pendingIntent = intentControl(activity, tipoConsumo, limite, codArduino);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        // Cancela si habia una alarma anterior para el mismo tipo de consumo
        if(alarmManager != null){
            alarmManager.cancel(pendingIntent);
        }

        // Si no se setea un limite, no setea alarma
        if(limite != -1){
            // Para que el primer control lo haga al minuto
            long primerTrigger = Calendar.getInstance().getTimeInMillis() + 60 * 1000;
            // Setea alarma que controla cada 5 min TODO poner otro intervalo
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, primerTrigger,
                    5 * 60 * 1000, pendingIntent);

            Log.d("ConfigNotifFragment", "Se seteo la alarma " + tipoConsumo.toString() + " limite " + limite);
        }
    }

    /**
     * Intent a ejecutar cuando se dispara la alarma
     */
    public static PendingIntent intentControl(Activity activity, TipoConsumo tipoConsumo,
                                              Integer limite, Integer codArduino) {
        Intent intent = new Intent(activity, ControlLimites.class);
        intent.putExtra(ControlLimites.ARG_TIPO, tipoConsumo.getId());
        intent.putExtra(ControlLimites.ARG_LIMITE, limite);
        intent.putExtra(ControlLimites.ARG_COD_ARDUINO, codArduino);

        PendingIntent pendingIntent;
        if(tipoConsumo == TipoConsumo.ELECTRICIDAD){
            pendingIntent = PendingIntent.getBroadcast(activity, CODIGO_CONTROL_ELEC,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(activity, CODIGO_CONTROL_AGUA,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    /**
     * Elimina la alarma (control) para el tipo de consumo dado, en caso que exista
     * @param activity
     * @param idTipoConsumo
     */
    public static void eliminarControl(Activity activity, int idTipoConsumo){
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        PendingIntent intent = ConfigNotifFragment.intentControl(activity, TipoConsumo.getById(idTipoConsumo), 0, 0);
        if(alarmManager != null){
            Log.d("ConfigNotifFragment", "Se cancelo una alarma " + TipoConsumo.getById(idTipoConsumo));
            alarmManager.cancel(intent);
        }
    }

    @Override
    public void inicioRequest() {
        // Como se hace 2 request a la vez, el if evita que se creen dos progressDialog
        if(progressDialog == null){
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
        }
    }

    @Override
    public void finRequest(JSONObject json) {
        limitesActualizados = limitesActualizados + 1;

        // Cierra el progressDialog recien cuando se actualizan los 2, por eso el contador
        if(progressDialog != null && limitesActualizados == 2){
            conectando = false;
            limitesActualizados = 0;
            progressDialog.dismiss();
            progressDialog = null;
        }

        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    Toast.makeText(getActivity(), "Datos guardados" , Toast.LENGTH_SHORT).show();
                } else if(json.getString("status").equals("error")){
                    Toast.makeText(getActivity(), json.getString("data") , Toast.LENGTH_SHORT).show();
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
