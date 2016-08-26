package g507.controldeconsumo;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class LoginFragment extends Fragment {

    private static final String ARG_USERNAME = "arg_username";

    private static final String USUARIO_TEST = "admin";
    private static final String PASS_TEST = "admin123";

    private UserLoginTask taskLogin = null;

    // UI references.
    private View viewPrincipal;
    private EditText txtUsuario;
    private EditText txtPassword;
    private Button btnIngresar;
    private Button btnRegistrarse;
    private TextView linkRecupContr;

    private ProgressDialog progressDialog;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        boolean cancelar = false;
        View campoConError = null;

        if (taskLogin != null) {
            return;
        }

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
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_autenticando), true);
            taskLogin = new UserLoginTask(usuario, password);
            taskLogin.execute((Void) null);
        }
    }

    private void abrirRegistro() {
        startActivity(new Intent(getActivity(), RegistroActivity.class));
    }

    private void abrirRecuperarPass() {
        String usuario = txtUsuario.getText().toString();

        if (TextUtils.isEmpty(usuario)) {
            txtUsuario.setError(getString(R.string.error_campo_requerido));
            txtUsuario.requestFocus();
            return;
        }

        //TODO validar que exista el usuario

        Intent intent = new Intent(getActivity(), CambiarPassActivity.class);
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, usuario);
        intent.putExtras(args);
        startActivity(intent);
    }

    private boolean passwordValida(String password) {
        return password.length() >= 8;
    }

    public void guardarUsuarioLogueado(String user) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putString(getString(R.string.pref_sesion_inic), user).apply();
    }

    private void cargarMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    //Se usa un AsyncTask para crear un thread nuevo y no conectarse con la BD en el thread UI
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String usuario;
        private final String password;

        UserLoginTask(String usuario, String password) {
            this.usuario = usuario;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: conectar con la bd

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return (usuario.equals(USUARIO_TEST) && password.equals(PASS_TEST));
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            taskLogin = null;
            progressDialog.dismiss();

            if (success) {
                guardarUsuarioLogueado(usuario);

                cargarMainActivity();
            } else {
                txtPassword.setError(getString(R.string.error_pass_incorrecta));
                txtPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            taskLogin = null;
            progressDialog.dismiss();
        }
    }
}