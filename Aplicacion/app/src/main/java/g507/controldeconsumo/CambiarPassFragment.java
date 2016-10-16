package g507.controldeconsumo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.preference.PreferenceManager;
import android.app.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.TaskListener;

import g507.controldeconsumo.modelo.PreguntaSeguridad;

public class CambiarPassFragment extends Fragment implements TaskListener {
    private static final String ARG_FORM = "num_form";

    private View view;
    private ViewFlipper viewFlipper;
    private TextView txtVPreg;
    private EditText txtRespuesta;
    private Button btnValidarResp;
    private EditText txtNuevaPass;
    private EditText txtConfNuevaPass;
    private Button btnNuevaPass;

    // Datos del usuario al que se va a cambiar la contraseña
    private Integer idUsuario = -1;
    private PreguntaSeguridad pregunta;
    private String respuesta;

    private boolean conectando = false;
    private ProgressDialog progressDialog;

    public CambiarPassFragment() {
        // Required empty public constructor
    }

    public static CambiarPassFragment newInstance(Integer idUsuario, Integer idPregunta, String respPreg) {
        CambiarPassFragment fragment = new CambiarPassFragment();
        Bundle args = new Bundle();
        args.putInt(CambiarPassActivity.ARG_ID_USUARIO, idUsuario);
        args.putInt(CambiarPassActivity.ARG_ID_PREG, idPregunta);
        args.putString(CambiarPassActivity.ARG_RESP_PREG, respPreg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUsuario = getArguments().getInt(CambiarPassActivity.ARG_ID_USUARIO);
            pregunta = PreguntaSeguridad.getPreguntaById(getArguments().getInt(CambiarPassActivity.ARG_ID_PREG));
            respuesta = getArguments().getString(CambiarPassActivity.ARG_RESP_PREG);
        }

        //Con esto carga el formulario del paso en que estaba al rotar la pantalla
        //sin esto, si roto la pantalla vuelve siempre al paso 1
        if(savedInstanceState != null){
            int numForm = savedInstanceState.getInt(ARG_FORM);
            viewFlipper.setDisplayedChild(numForm);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cambiar_pass, container, false);

        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);
        txtVPreg = (TextView) view.findViewById(R.id.txtVPreg);
        txtRespuesta = (EditText) view.findViewById(R.id.txtRespuesta);
        btnValidarResp = (Button) view.findViewById(R.id.btnValidarResp);
        txtNuevaPass = (EditText) view.findViewById(R.id.txtNuevaPass);
        txtConfNuevaPass = (EditText) view.findViewById(R.id.txtConfNuevaPass);
        btnNuevaPass = (Button) view.findViewById(R.id.btnCambiarPass);

        btnValidarResp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarResp();
            }
        });

        btnNuevaPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarNuevaPass();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        if(pregunta != null){
            txtVPreg.setText(pregunta.toString());
        }

        super.onStart();
    }

    /**
     * Valida que la respuesta ingresada coincida con la registrada,
     * marca el error en caso contrario
     */
    private void validarResp(){
        String respIngresada = txtRespuesta.getText().toString();

        if(respIngresada.equals(respuesta)){
            //Habilita el prox paso
            viewFlipper.showNext();
        } else {
            txtRespuesta.setError(getString(R.string.error_resp_incorrecta));
            txtRespuesta.requestFocus();
        }
    }

    /**
     * Valida las contraseñas ingresadas y guarda en la BD
     */
    private void guardarNuevaPass(){
        if(conectando)
            return;

        boolean cancelar = false;
        View campoConError = null;

        String pass = txtNuevaPass.getText().toString();
        String passConf = txtConfNuevaPass.getText().toString();

        if(TextUtils.isEmpty(passConf)){
            txtConfNuevaPass.setError(getString(R.string.error_campo_requerido));
            campoConError = txtConfNuevaPass;
            cancelar = true;
        } else if(!passConf.equals(pass)){
            txtConfNuevaPass.setError(getString(R.string.error_no_coinciden));
            campoConError = txtConfNuevaPass;
            cancelar = true;
        }

        if(TextUtils.isEmpty(pass)){
            txtNuevaPass.setError(getString(R.string.error_campo_requerido));
            campoConError = txtNuevaPass;
            cancelar = true;
        } else if(!passwordValida(pass)){
            txtNuevaPass.setError(getString(R.string.error_pass_invalida));
            campoConError = txtNuevaPass;
            cancelar = true;
        }

        if(cancelar){
            campoConError.requestFocus();
        } else{
            if(idUsuario != -1)
                new TaskRequestUrl(this).execute(ConstructorUrls.cambiarContraseña(idUsuario, pass), "PUT");
            else
                Toast.makeText(getActivity(), "No hay un id de usuario asociado", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean passwordValida(String password){
        return password.length() >= 8;
    }

    private void cargarMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Guarda el numero del paso en que esta al rotar la pantalla
        int numForm = viewFlipper.getDisplayedChild();
        outState.putInt(ARG_FORM, numForm);
    }

    @Override
    public void inicioRequest() {
        conectando = true;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {
        conectando = false;

        if(progressDialog != null)
            progressDialog.dismiss();

        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    Toast.makeText(getActivity(), "Contraseña cambiada" , Toast.LENGTH_SHORT).show();

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
    }
}
