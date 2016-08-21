package g507.controldeconsumo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CambiarPassActivity extends AppCompatActivity {

    private static final String ARG_USERNAME = "arg_username";

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pass);

        if(savedInstanceState == null){
            Bundle args = getIntent().getExtras();
            if(args != null)
                username = args.getString(ARG_USERNAME);

            CambiarPassFragment fragment = CambiarPassFragment.newInstance(username);
            getSupportFragmentManager().beginTransaction().add(
                    R.id.container_cambiar_pass, fragment).commit();
        }
    }
}
