package g507.controldeconsumo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!sesionIniciada()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int idItemSelecc = item.getItemId();

        switch(idItemSelecc){
            case R.id.asoc_arduino:
                cargarFragment(new AsociarFragment());
                break;
            case R.id.config:
                cargarFragment(new ConfigFragment());
                break;
            case R.id.consumo_actual:
                cargarFragment(new ConsActualFragment());
                break;
            case R.id.consumo_acumulado:
                cargarFragment(new ConsAcumFragment());
                break;
            case R.id.prox_factura:
                cargarFragment(new ProxFacFragment());
                break;
            case R.id.estadisticas:
                cargarFragment(new EstadistFragment());
                break;
            case R.id.cerrar_sesion:
                new AlertDialog.Builder(this)
                        .setTitle("Cerrar sesión")
                        .setMessage("Desea cerrar sesión?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                prefs.edit().putString(getString(R.string.pref_sesion_inic), "").apply();
                                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                                finish();
                            }})

                        .setNegativeButton("No", null).show();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * True o false segun si hay una sesion iniciada
     */
    private boolean sesionIniciada(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String usuarioLogueado = prefs.getString(getString(R.string.pref_sesion_inic), "");

        if(!usuarioLogueado.isEmpty()){
            return true;
        } else{
            return false;
        }
    }

    private void cargarFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                fragment).commit();
    }
}
