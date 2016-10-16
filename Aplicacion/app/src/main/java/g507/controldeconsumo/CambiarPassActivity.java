package g507.controldeconsumo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CambiarPassActivity extends AppCompatActivity {
    public static final String ARG_ID_USUARIO = "arg_id_usuario";
    public static final String ARG_ID_PREG = "arg_id_preg";
    public static final String ARG_RESP_PREG = "arg_resp_preg";

    private Integer idUsuario;
    private Integer idPregSeguridad;
    private String respPregSeguridad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pass);

        if(savedInstanceState == null){
            Bundle args = getIntent().getExtras();
            if(args != null) {
                idUsuario = args.getInt(ARG_ID_USUARIO);
                idPregSeguridad = args.getInt(ARG_ID_PREG);
                respPregSeguridad = args.getString(ARG_RESP_PREG);
            }

            CambiarPassFragment fragment = CambiarPassFragment.newInstance(idUsuario, idPregSeguridad, respPregSeguridad);
            getSupportFragmentManager().beginTransaction().add(
                    R.id.container_cambiar_pass, fragment).commit();
        }
    }
}
