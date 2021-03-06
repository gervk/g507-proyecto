package g507.controldeconsumo;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.modelo.ServicioAgua;
import g507.controldeconsumo.modelo.TipoConsumo;

public class LoginFragment extends Fragment implements TaskListener{

    private View viewPrincipal;
    private EditText txtUsuario;
    private EditText txtPassword;
    private Button btnIngresar;
    private Button btnRegistrarse;
    private TextView linkRecupContr;

    private ProgressDialog progressDialog;
    private boolean autenticando = false;
    private boolean cargandoDatosCambioPass = false;

    public LoginFragment() {
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
        if(autenticando || cargandoDatosCambioPass)
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewPrincipal = inflater.inflate(R.layout.fragment_login, container, false);

        txtUsuario = (EditText) viewPrincipal.findViewById(R.id.txtUsuario);

        txtPassword = (EditText) viewPrincipal.findViewById(R.id.txtPassword);
        //Para que ingrese al tocar el enter del teclado
        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ingresar();
                    return true;
                }
                return false;
            }
        });

        linkRecupContr = (TextView) viewPrincipal.findViewById(R.id.linkRecupContr);
        linkRecupContr.setPaintFlags(linkRecupContr.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        linkRecupContr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirRecuperarPass();
            }
        });

        btnIngresar = (Button) viewPrincipal.findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingresar();
            }
        });

        btnRegistrarse = (Button) viewPrincipal.findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarActivityRegistro();
            }
        });

        return viewPrincipal;
    }

    private void ingresar() {
        if(autenticando)
            return;

        boolean cancelar = false;
        View campoConError = null;

        // Resetea msjs de error
        txtUsuario.setError(null);
        txtPassword.setError(null);

        //Lectura de valores ingresados
        String usuario = txtUsuario.getText().toString();
        String password = txtPassword.getText().toString();

        //Validacion de campos no vacios y pass de 8 caracteres minimo
        //Valido de abajo a arriba para que marque el error del de mas arriba
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getString(R.string.error_campo_requerido));
            campoConError = txtPassword;
            cancelar = true;
        } else {
            if(!Utils.alfanumericoSinEspacios(password)){
                txtPassword.setError(getString(R.string.error_campo_formato));
                campoConError = txtPassword;
                cancelar = true;
            } else{
                if (!passwordValida(password)) {
                    txtPassword.setError(getString(R.string.error_pass_invalida));
                    campoConError = txtPassword;
                    cancelar = true;
                }
            }
        }

        if (TextUtils.isEmpty(usuario)) {
            txtUsuario.setError(getString(R.string.error_campo_requerido));
            campoConError = txtUsuario;
            cancelar = true;
        } else if (!Utils.alfanumericoSinEspacios(usuario)){
            txtUsuario.setError(getString(R.string.error_campo_formato));
            campoConError = txtUsuario;
            cancelar = true;
        }

        if (cancelar) {
            //En caso de error, no loguea y hace foco en el primer campo con error
            campoConError.requestFocus();
        } else {
            if(Utils.conexionAInternetOk(getActivity())){
                autenticando = true;
                new TaskRequestUrl(this).execute(ConstructorUrls.login(usuario, password), "GET");
            } else{
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirRecuperarPass() {
        if(cargandoDatosCambioPass)
            return;

        String usuario = txtUsuario.getText().toString();

        if (TextUtils.isEmpty(usuario)) {
            txtUsuario.setError(getString(R.string.error_campo_requerido));
            txtUsuario.requestFocus();
            return;
        }

        if(Utils.conexionAInternetOk(getActivity())){
            cargandoDatosCambioPass = true;
            new TaskRequestUrl(this).execute(ConstructorUrls.getUsuario(usuario), "GET");
        } else{
            Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean passwordValida(String password) {
        return password.length() >= 8;
    }

    /**
     * Guarda en la config local los datos del usuario y setea las alertas de consumo correspondientes
     * @param idUsuario
     * @param codigoArduino
     */
    private void guardarDatosUsuario(String nombreUsuario, Integer idUsuario, Integer codigoArduino,
                                     Integer limiteAgua, Integer limiteElect) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putString(getString(R.string.pref_nombre_usuario), nombreUsuario).apply();
        prefs.edit().putInt(getString(R.string.pref_sesion_inic), idUsuario).apply();
        prefs.edit().putInt(getString(R.string.pref_id_arduino), codigoArduino).apply();
        prefs.edit().putInt(getString(R.string.pref_limite_agua), limiteAgua).apply();
        prefs.edit().putInt(getString(R.string.pref_limite_elect), limiteElect).apply();

        // Seteo de alertas para los limites configurados
        if(codigoArduino != -1 && limiteElect != -1){
            ConfigNotifFragment.setControl(getActivity(), TipoConsumo.ELECTRICIDAD, limiteElect, codigoArduino);
        }
        if(codigoArduino != -1 && limiteAgua != -1){
            ConfigNotifFragment.setControl(getActivity(), TipoConsumo.AGUA, limiteAgua, codigoArduino);
        }
    }

    /**
     * Guarda en la config local el id de empresa de electricidad y la fecha de la ultima factura
     */
    private void guardarDatosConfigElec(Integer idEmpresa, String fechaFactura) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putInt(getString(R.string.pref_empresa_elect), idEmpresa).apply();
        prefs.edit().putString(getString(R.string.pref_fecha_fact_elect), fechaFactura).apply();
    }

    /**
     * Guarda en la config local los coeficientes del servicio de agua
     */
    private void guardarDatosConfigAgua(ServicioAgua servicio){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

    private void cargarMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private void cargarCambiarPassActivity(Integer idUsuario, Integer idPregSeguridad, String respPregSeguridad){
        Intent intent = new Intent(getActivity(), CambiarPassActivity.class);
        Bundle args = new Bundle();
        // Parametros con id de usuario, pregunta y respuesta de seguridad
        args.putInt(CambiarPassActivity.ARG_ID_USUARIO, idUsuario);
        args.putInt(CambiarPassActivity.ARG_ID_PREG, idPregSeguridad);
        args.putString(CambiarPassActivity.ARG_RESP_PREG, respPregSeguridad);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void cargarActivityRegistro() {
        startActivity(new Intent(getActivity(), RegistroActivity.class));
    }

    @Override
    public void inicioRequest() {
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {
        // Fin autenticacion
        if(autenticando){
            autenticando = false;

            if(progressDialog != null)
                progressDialog.dismiss();

            if(json != null){
                try {
                    if(json.getString("status").equals("ok")){
                        JSONObject data = json.getJSONObject("data");
                        JSONObject user = data.getJSONObject("user");

                        String nombreUsuario = user.getString("username");
                        Integer idUsuario = user.getInt("id");
                        // optInt devuelve un valor dado en caso que sea null
                        Integer codArduino = user.optInt("codigo_arduino", -1);
                        Integer limiteAgua = user.optInt("limite_agua", -1);
                        Integer limiteElect = user.optInt("limite_luz", -1);

                        if(!data.isNull("config_elec")){
                            JSONObject configElec = data.getJSONObject("config_elec");
                            Integer idEmpresa = configElec.getInt("id_empresa");
                            String fechaFactura = configElec.getString("ultima_factura");
                            guardarDatosConfigElec(idEmpresa, fechaFactura);
                        }

                        if(!data.isNull("config_agua")){
                            JSONObject configAgua = data.getJSONObject("config_agua");
                            ServicioAgua servicioAgua = new ServicioAgua(configAgua.getInt("id"), configAgua.getDouble("k"),configAgua.getDouble("zf"),
                                    configAgua.getDouble("tgdf"), configAgua.getDouble("sc"),configAgua.getDouble("ef"), configAgua.getDouble("st"),
                                    configAgua.getDouble("aud"), configAgua.getInt("fs"),configAgua.getInt("cl"));
                            servicioAgua.setFecFact(configAgua.getString("ultima_factura"));
                            guardarDatosConfigAgua(servicioAgua);
                        }

                        guardarDatosUsuario(nombreUsuario, idUsuario, codArduino, limiteAgua, limiteElect);
                        cargarMainActivity();
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
            return;
        }

        // Fin carga datos para cambiar contraseña
        if(cargandoDatosCambioPass){
            cargandoDatosCambioPass = false;

            if(progressDialog != null)
                progressDialog.dismiss();

            if(json != null){
                try {
                    if(json.getString("status").equals("ok")){
                        JSONObject data = json.getJSONObject("data").getJSONObject("user");
                        Integer idUsuario = data.getInt("id");
                        Integer idPregSeguridad = data.getInt("id_preg_seguridad");
                        String respPregSeguridad = data.getString("resp_preg_seguridad");

                        cargarCambiarPassActivity(idUsuario, idPregSeguridad, respPregSeguridad);
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
            return;
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