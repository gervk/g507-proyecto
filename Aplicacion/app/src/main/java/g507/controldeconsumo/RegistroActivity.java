package g507.controldeconsumo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RegistroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        if(savedInstanceState == null){
            RegistroFragment fragment = new RegistroFragment();
            getSupportFragmentManager().beginTransaction().add(
                    R.id.container_registro, fragment).commit();
        }
    }
}
