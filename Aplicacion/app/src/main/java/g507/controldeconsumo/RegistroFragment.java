package g507.controldeconsumo;

import android.os.Bundle;
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
import android.widget.ViewFlipper;

import g507.controldeconsumo.modelo.PreguntaSeguridad;

public class RegistroFragment extends Fragment {

    private View view;
    private ViewFlipper viewFlipper;
    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtConfPassword;
    private Spinner spinPregSeg;
    private EditText txtRespPreg;
    private Button btnPaso2Reg;
    private EditText txtNombre;
    private EditText txtApellido;
    private Spinner spinLocalidad;
    private Spinner spinBarrio;
    private Button btnRegistrar;


    public RegistroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registro, container, false);

        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);
        txtUsername = (EditText) view.findViewById(R.id.txtUsername);
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        txtConfPassword = (EditText) view.findViewById(R.id.txtConfPassword);
        spinPregSeg = (Spinner) view.findViewById(R.id.spinPregSeg);
        txtRespPreg = (EditText) view.findViewById(R.id.txtRespPreg);
        btnPaso2Reg = (Button) view.findViewById(R.id.btnPaso2Reg);
        txtNombre = (EditText) view.findViewById(R.id.txtNombre);
        txtApellido = (EditText) view.findViewById(R.id.txtApellido);
        spinLocalidad = (Spinner) view.findViewById(R.id.spinLocalidad);
        spinBarrio = (Spinner) view.findViewById(R.id.spinBarrio);
        btnRegistrar = (Button) view.findViewById(R.id.btnRegistrar);

        //Set de opciones en los spinner
        ArrayAdapter<PreguntaSeguridad> preguntasAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, PreguntaSeguridad.values());
        spinPregSeg.setAdapter(preguntasAdapter);

        btnPaso2Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasarASegundoPaso();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
            }
        });

        return view;
    }

    private void pasarASegundoPaso(){
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
        } else if(!passwordValida(password)){
            txtPassword.setError(getString(R.string.error_pass_invalida));
            campoConError = txtPassword;
            cancelar = true;
        }

        if(TextUtils.isEmpty(email)){
            txtEmail.setError(getString(R.string.error_campo_requerido));
            campoConError = txtEmail;
            cancelar = true;
        }

        if(TextUtils.isEmpty(username)){
            txtUsername.setError(getString(R.string.error_campo_requerido));
            campoConError = txtUsername;
            cancelar = true;
        }

        //TODO validar con BD que no se repita el nombre de usuario ni el mail

        if(cancelar){
            campoConError.requestFocus();
        } else{
            viewFlipper.showNext();
        }
    }

    private void registrar(){
        boolean cancelar = false;
        View campoConError = null;

        txtNombre.setError(null);
        txtApellido.setError(null);

        String nombre = txtNombre.getText().toString();
        String apellido = txtApellido.getText().toString();
        String localidad = (String) spinLocalidad.getSelectedItem();
        String barrio = (String) spinBarrio.getSelectedItem();

        if(TextUtils.isEmpty(barrio)){
            //fixme no se puede hacer foco en los spinner ni el setError como en los txt
            campoConError = spinBarrio;
            cancelar = true;
        }
        if(TextUtils.isEmpty(localidad)){
            //fixme no se puede hacer foco en los spinner ni el setError como en los txt
            campoConError = spinLocalidad;
            cancelar = true;
        }
        if(TextUtils.isEmpty(apellido)){
            txtApellido.setError(getString(R.string.error_campo_requerido));
            campoConError = txtApellido;
            cancelar = true;
        }
        if(TextUtils.isEmpty(nombre)){
            txtNombre.setError(getString(R.string.error_campo_requerido));
            campoConError = txtNombre;
            cancelar = true;
        }

        if(cancelar){
            campoConError.requestFocus();
        } else{
            //TODO registrar en bd
            Toast.makeText(getActivity(), R.string.error_servidor_no_disp, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean passwordValida(String password){
        return password.length() >= 8;
    }
}
