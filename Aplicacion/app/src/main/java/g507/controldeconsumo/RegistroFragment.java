package g507.controldeconsumo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.modelo.PreguntaSeguridad;

public class RegistroFragment extends Fragment implements TaskListener {

    private View view;
    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtConfPassword;
    private Spinner spinPregSeg;
    private EditText txtRespPreg;
    private Button btnRegistrar;

    private boolean guardandoDatos = false;
    private ProgressDialog progressDialog;

    public RegistroFragment() {
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
        if(guardandoDatos)
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registro, container, false);

        txtUsername = (EditText) view.findViewById(R.id.txtUsername);
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        txtConfPassword = (EditText) view.findViewById(R.id.txtConfPassword);
        spinPregSeg = (Spinner) view.findViewById(R.id.spinPregSeg);
        txtRespPreg = (EditText) view.findViewById(R.id.txtRespPreg);
        btnRegistrar = (Button) view.findViewById(R.id.btnRegistrar);

        //Set de opciones en los spinner
        ArrayAdapter<PreguntaSeguridad> preguntasAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, PreguntaSeguridad.values());
        spinPregSeg.setAdapter(preguntasAdapter);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //registrar();
                cargarMainActivity();
            }
        });

        return view;
    }

    private void registrar(){
        if(guardandoDatos)
            return;

        boolean cancelar = false;
        View campoConError = null;

        //Resetea msjs de error
        txtUsername.setError(null);
        txtPassword.setError(null);
        txtConfPassword.setError(null);
        txtRespPreg.setError(null);

        //Lectura valores ingresados
        String username = txtUsername.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String confPassword = txtConfPassword.getText().toString();
        PreguntaSeguridad pregunta = (PreguntaSeguridad) spinPregSeg.getSelectedItem();
        String respuesta = txtRespPreg.getText().toString();

        if(TextUtils.isEmpty(respuesta)){
            txtRespPreg.setError(getString(R.string.error_campo_requerido));
            campoConError = txtRespPreg;
            cancelar = true;
        }

        if(pregunta == null){
            //fixme no se puede hacer foco en los spinner ni el setError como en los txt
            campoConError = spinPregSeg;
            cancelar = true;
        }

        if(TextUtils.isEmpty(confPassword)){
            txtConfPassword.setError(getString(R.string.error_campo_requerido));
            campoConError = txtConfPassword;
            cancelar = true;
        } else if(!confPassword.equals(password)){
            txtConfPassword.setError(getString(R.string.error_no_coinciden));
            campoConError = txtConfPassword;
            cancelar = true;
        }

        if(TextUtils.isEmpty(password)){
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

        if(TextUtils.isEmpty(email)){
            txtEmail.setError(getString(R.string.error_campo_requerido));
            campoConError = txtEmail;
            cancelar = true;
        } else {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                txtEmail.setError(getString(R.string.error_campo_formato));
                campoConError = txtEmail;
                cancelar = true;
            }
        }

        if(TextUtils.isEmpty(username)){
            txtUsername.setError(getString(R.string.error_campo_requerido));
            campoConError = txtUsername;
            cancelar = true;
        } else if (!Utils.alfanumericoSinEspacios(username)){
            txtUsername.setError(getString(R.string.error_campo_formato));
            campoConError = txtUsername;
            cancelar = true;
        }

        if(cancelar){
            campoConError.requestFocus();
        } else{
            if(Utils.conexionAInternetOk(getActivity())){
                new TaskRequestUrl(this).execute(ConstructorUrls.registro(username, password, email, pregunta, respuesta), "POST");
            } else{
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean passwordValida(String password){
        return password.length() >= 8;
    }

    public void guardarUsuarioLogueado(Integer idUsuario) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putInt(getString(R.string.pref_sesion_inic), idUsuario).apply();
    }

    private void cargarMainActivity() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        //getActivity().finish();
    }

    @Override
    public void inicioRequest() {
        guardandoDatos = true;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {
        guardandoDatos = false;

        if(progressDialog != null)
            progressDialog.dismiss();

        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    Integer idUsuario = json.getJSONObject("data").getInt("id");

                    guardarUsuarioLogueado(idUsuario);
                    cargarMainActivity();
                } else if(json.getString("status").equals("error")) {
                    Toast.makeText(getActivity(), "Error en el registro" , Toast.LENGTH_SHORT).show();
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
