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
                abrirRegistro();
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
        } else if (!passwordValida(password)) {
            txtPassword.setError(getString(R.string.error_pass_invalida));
            campoConError = txtPassword;
            cancelar = true;
        }

        if (TextUtils.isEmpty(usuario)) {
            txtUsuario.setError(getString(R.string.error_campo_requerido));
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

    private void abrirRegistro() {
        startActivity(new Intent(getActivity(), RegistroActivity.class));
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
     * Guarda en la config local los datos del usuario
     * @param idUsuario
     * @param codigoArduino
     */
    private void guardarDatosUsuario(Integer idUsuario, Integer codigoArduino,
                                     Integer limiteAgua, Integer limiteElect) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Lo que se vaya agregando aca hay que ponerlo en el borrarDatosUsuario() de MainActivity
        prefs.edit().putInt(getString(R.string.pref_sesion_inic), idUsuario).apply();
        prefs.edit().putInt(getString(R.string.pref_id_arduino), codigoArduino).apply();
        prefs.edit().putInt(getString(R.string.pref_limite_agua), limiteAgua).apply();
        prefs.edit().putInt(getString(R.string.pref_limite_elect), limiteElect).apply();

        if(codigoArduino != -1 && limiteElect != -1){
            ConfigNotifFragment.setControl(getActivity(), TipoConsumo.ELECTRICIDAD, limiteElect, codigoArduino);
        }
        if(codigoArduino != -1 && limiteAgua != -1){
            ConfigNotifFragment.setControl(getActivity(), TipoConsumo.AGUA, limiteAgua, codigoArduino);
        }
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
                        JSONObject data = json.getJSONObject("data").getJSONObject("user");
                        Integer idUsuario = data.getInt("id");
                        // optInt devuelve un valor dado en caso que sea null
                        Integer codArduino = data.optInt("codigo_arduino", -1);
                        Integer limiteAgua = data.optInt("limite_agua", -1);
                        Integer limiteElect = data.optInt("limite_luz", -1);

                        guardarDatosUsuario(idUsuario, codArduino, limiteAgua, limiteElect);

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
                        Toast.makeText(getActivity(), "No existe el usuario" , Toast.LENGTH_SHORT).show();
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