package g507.controldeconsumo;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.modelo.Periodo;
import g507.controldeconsumo.modelo.TipoConsumo;

public class ConsAcumFragment extends Fragment implements TaskListener{

    private View view;
    private RadioGroup rgrpServicio;
    private RadioButton rbtnElect;
    private RadioButton rbtnAgua;
    private Spinner spinPeriodo;
    private Button btnConsultar;
    private TextView txtResultadoAcum;

    private boolean conectando = false;
    private ProgressDialog progressDialog;

    public ConsAcumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Para que mantenga la instancia del fragment ante una recreacion del activity (rotacion)
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Si se esta volviendo de una rotacion de pantalla y sigue el request, muestra msj de espera
        if(conectando)
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cons_acum, container, false);
        rgrpServicio = (RadioGroup) view.findViewById(R.id.rgrpServicio);
        rbtnElect = (RadioButton) view.findViewById(R.id.rbtnElect);
        rbtnAgua = (RadioButton) view.findViewById(R.id.rbtnAgua);
        spinPeriodo = (Spinner) view.findViewById(R.id.spinPeriodo);
        btnConsultar = (Button) view.findViewById(R.id.btnConsAcum);
        txtResultadoAcum = (TextView) view.findViewById(R.id.txtVResulAcu);

        //Set de opciones en el spinner de periodo
        ArrayAdapter<Periodo> periodosAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, Periodo.values());
        spinPeriodo.setAdapter(periodosAdapter);

        //Listener para el boton consultar
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultar();
            }
        });

        return view;
    }

    private void consultar(){
        Calendar cal;
        TipoConsumo tipoServicio;
        String fechaIni;
        String fechaFin;
        int idArduino;

        if(conectando)
            return;

        if(rgrpServicio.getCheckedRadioButtonId() == -1){
            Toast.makeText(getActivity(), R.string.error_selecc_servicio, Toast.LENGTH_SHORT).show();
            return;
        }

        if(spinPeriodo.getSelectedItem() == null){
            Toast.makeText(getActivity(), R.string.error_selecc_periodo, Toast.LENGTH_SHORT).show();
            return;
        }

        if(rbtnElect.isChecked()) {
            tipoServicio = TipoConsumo.ELECTRICIDAD;
        }
        else {
            tipoServicio = TipoConsumo.AGUA;
        }


        cal = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        fechaFin = dateFormat.format(cal.getTime());

        // Para que sea desde las 00hs
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        switch ((Periodo) spinPeriodo.getSelectedItem()){
            case DIA:
                //cal.add(Calendar.DATE, -1);
                // no hace falta cambiar nada
                break;
            case SEMANA:
                //cal.add(Calendar.DATE, -7);
                cal.set(Calendar.DAY_OF_WEEK, 2); // desde el dia lunes de la semana actual
                break;
            case MES:
                //cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1); // desde el primer dia del mes actual
                break;
            case BIMESTRE:
                //cal.add(Calendar.MONTH, -2);
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1); // desde el 1° dia del mes anterior
                break;
        }

        fechaIni = dateFormat.format(cal.getTime());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //había que ponerle un valor default, puse que sea -1
        idArduino = prefs.getInt(getString(R.string.pref_id_arduino), -1);

        if(idArduino != -1){
            if(Utils.conexionAInternetOk(getActivity())){
                String url = ConstructorUrls.consumoAcumulado(idArduino, tipoServicio,
                        Timestamp.valueOf(fechaIni), Timestamp.valueOf(fechaFin));
                new TaskRequestUrl(this).execute(url, "GET");
            } else {
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_no_arduino), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void inicioRequest() {
        conectando = true;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {
        conectando = false;

        if(progressDialog != null)
            progressDialog.dismiss();

        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    txtResultadoAcum = (TextView) view.findViewById(R.id.txtVResulAcu);
                    txtResultadoAcum.setText(new DecimalFormat("0.##").format(json.getDouble("data"))+" KWh");
                } else if(json.getString("status").equals("error")){
                    Toast.makeText(getActivity(), "Datos incorrectos" , Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), getString(R.string.error_traducc_datos) , Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_inesperado_serv) , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        // Cierra el progressDialog si se saca el fragment del activity (cuando se rota), sino tira excepcion
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDetach();
    }
}
