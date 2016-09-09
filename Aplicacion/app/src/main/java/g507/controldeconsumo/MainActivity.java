package g507.controldeconsumo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String ARG_FONDO = "arg_fondo";

    private ImageView imagen;
    private boolean mostrarFondo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!sesionIniciada()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            imagen = (ImageView) this.findViewById(R.id.imgFondo);

            //Si habia una instancia anterior, carga la variable para mostrar o no el fondo
            if(savedInstanceState != null){
                mostrarFondo = savedInstanceState.getBoolean(ARG_FONDO);
            }
            if(!mostrarFondo){
                imagen.setVisibility(View.GONE);
            }
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

/* Lo dejo comentado asi no se crea en la barra el boton de settings
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
*/

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        imagen.setVisibility(View.GONE);
        mostrarFondo = false;

        item.setChecked(true);
        int idItemSelecc = item.getItemId();

        switch (idItemSelecc) {
            case R.id.asoc_arduino:
                cargarFragment(new AsociarFragment(), getString(R.string.title_frag_asoc_arduino),false);
                break;
            case R.id.config:
                cargarFragment(new ConfigFragment(), getString(R.string.title_frag_config), true);
                break;
            case R.id.consumo_actual:
                cargarFragment(new ConsActualFragment(), getString(R.string.title_frag_cons_actual), true);
                break;
            case R.id.consumo_acumulado:
                cargarFragment(new ConsAcumFragment(), getString(R.string.title_frag_cons_acum), true);
                break;
            case R.id.prox_factura:
                cargarFragment(new ProxFacFragment(), getString(R.string.title_frag_prox_factura), true);
                break;
            case R.id.estadisticas:
                cargarFragment(new EstadistFragment(), getString(R.string.title_frag_estadisticas), true);
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
                            }
                        })

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
    private boolean sesionIniciada() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String usuarioLogueado = prefs.getString(getString(R.string.pref_sesion_inic), "");

        return !usuarioLogueado.isEmpty();
    }

    private void cargarFragment(Fragment fragment, String titulo, boolean rotable) {
        setTitle(titulo);
        if (rotable) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                fragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Guarda temporalmente la variable mostrarFondo para la prox que se cree la vista, ej cuando se rota
        outState.putBoolean(ARG_FONDO, mostrarFondo);
        super.onSaveInstanceState(outState);
    }
}
