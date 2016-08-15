package g507.controldeconsumo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(savedInstanceState == null){
            LoginFragment fragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(
                    R.id.container_login, fragment).commit();
        }
    }

}