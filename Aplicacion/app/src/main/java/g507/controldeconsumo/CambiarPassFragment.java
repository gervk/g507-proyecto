package g507.controldeconsumo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import g507.controldeconsumo.modelo.PreguntaSeguridad;

public class CambiarPassFragment extends Fragment {
    private static final String ARG_USERNAME = "arg_username";
    private static final int ID_PREG_TEST = 1;
    private static final String RESP_TEST = "abcd";

    //Usuario al que se le va a cambiar la contraseña
    private String username;

    private PreguntaSeguridad pregunta;
    private String respuesta;

    private View view;
    private ViewFlipper viewFlipper;
    private TextView txtVPreg;
    private EditText txtRespuesta;
    private Button btnValidarResp;
    private EditText txtNuevaPass;
    private EditText txtConfNuevaPass;
    private Button btnNuevaPass;

    public CambiarPassFragment() {
        // Required empty public constructor
    }

    public static CambiarPassFragment newInstance(String username) {
        CambiarPassFragment fragment = new CambiarPassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
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
        cargarPregunta();

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
            //TODO guardar en BD
            Toast.makeText(getActivity(), getString(R.string.error_servidor_no_disp), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean passwordValida(String password){
        return password.length() >= 8;
    }

    /**
     * Se conecta a la BD para buscar la pregunta configurada por el usuario y la respuesta dada
     */
    private void cargarPregunta(){
        //TODO pregunta-respuesta de ejemplo, eliminar y buscar en BD
        int idPreg = ID_PREG_TEST;
        pregunta = PreguntaSeguridad.getPreguntaById(idPreg);
        respuesta = RESP_TEST;

        if(pregunta != null){
            txtVPreg.setText(pregunta.toString());
        }
    }
}
