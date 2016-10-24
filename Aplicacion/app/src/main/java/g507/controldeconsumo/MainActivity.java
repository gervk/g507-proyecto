package g507.controldeconsumo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import g507.controldeconsumo.modelo.TipoConsumo;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ARG_FONDO = "arg_fondo";
    private static final int CODIGO_REQUEST_CAMARA = 001;

    private ImageView imagen;
    private ImageView fondoInicio;
    private boolean mostrarFondo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carga los default de settings (se hace solo la primera vez)
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

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
            fondoInicio = (ImageView) this.findViewById(R.id.fondoInicio);

            //Si habia una instancia anterior, carga la variable para mostrar o no el fondo
            if(savedInstanceState != null){
                mostrarFondo = savedInstanceState.getBoolean(ARG_FONDO);
            } else{
                Bundle extras = getIntent().getExtras();
                if(extras != null){
                    int idTipoConsumo = extras.getInt(ControlLimites.ARG_TIPO_NOTIF, -1);
                    /* Si esta el argumento tipo notif, es porque se llego al activity al clickear una notif
                    entonces hay que eliminar el control de ese tipo de consumo para no repetir la notif
                    */
                    if(idTipoConsumo != -1){
                        ConfigNotifFragment.eliminarControl(this, idTipoConsumo);
                    }
                }
            }
            if(!mostrarFondo){
                imagen.setVisibility(View.GONE);
                fondoInicio.setVisibility(View.GONE);
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

        if (id == R.id.action_settings) {
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        imagen.setVisibility(View.GONE);
        fondoInicio.setVisibility(View.GONE);
        mostrarFondo = false;

        item.setChecked(true);
        int idItemSelecc = item.getItemId();

        switch (idItemSelecc) {
            case R.id.asoc_arduino:
                // Abre la pantalla asociar solo si tiene permiso para la cam, en caso que no, lo pide
                int tienePermisoCamara = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                if(tienePermisoCamara != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                            CODIGO_REQUEST_CAMARA);
                } else{
                    cargarFragment(new AsociarFragment(), getString(R.string.title_frag_asoc_arduino));
                }
                break;
            case R.id.config:
                cargarFragment(new ConfigFragment(), getString(R.string.title_frag_config));
                break;
            case R.id.consumo_actual:
                cargarFragment(new ConsActualFragment(), getString(R.string.title_frag_cons_actual));
                break;
            case R.id.consumo_acumulado:
                cargarFragment(new ConsAcumFragment(), getString(R.string.title_frag_cons_acum));
                break;
            case R.id.prox_factura:
                cargarFragment(new ProxFacFragment(), getString(R.string.title_frag_prox_factura));
                break;
            case R.id.estadisticas:
                cargarFragment(new EstadistFragment(), getString(R.string.title_frag_estadisticas));
                break;
            case R.id.cerrar_sesion:
                new AlertDialog.Builder(this)
                        .setTitle("Cerrar sesión")
                        .setMessage("Desea cerrar sesión?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                borrarDatosUsuario();
                                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                                finish();
                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                imagen.setVisibility(View.VISIBLE);
                                fondoInicio.setVisibility(View.VISIBLE);
                            }
                        }).show();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Borra lo guardado en la config local
     */
    private void borrarDatosUsuario() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefs.edit().putInt(getString(R.string.pref_sesion_inic), -1).apply();
        prefs.edit().putInt(getString(R.string.pref_id_arduino), -1).apply();
        prefs.edit().putInt(getString(R.string.pref_limite_elect), -1).apply();
        prefs.edit().putInt(getString(R.string.pref_limite_agua), -1).apply();

        ConfigNotifFragment.eliminarControl(this, TipoConsumo.ELECTRICIDAD.getId());
        ConfigNotifFragment.eliminarControl(this, TipoConsumo.AGUA.getId());
    }

    /**
     * True o false segun si hay una sesion iniciada
     */
    private boolean sesionIniciada() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Integer idusuarioLogueado = prefs.getInt(getString(R.string.pref_sesion_inic), -1);

        return idusuarioLogueado != -1;
    }

    private void cargarFragment(Fragment fragment, String titulo) {
        setTitle(titulo);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                fragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Guarda temporalmente la variable mostrarFondo para la prox que se cree la vista, ej cuando se rota
        outState.putBoolean(ARG_FONDO, mostrarFondo);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CODIGO_REQUEST_CAMARA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso agregado, no cargar fragment aca porque sino rompe
                } else {
                    Toast.makeText(this, "No se puede asociar el sensor sin la cámara", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
