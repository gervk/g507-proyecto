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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.modelo.TipoConsumo;
import g507.controldeconsumo.modelo.TipoEstadistica;

public class EstadistFragment extends Fragment implements TaskListener{
    //Libreria para los graficos http://www.android-graphview.org/

    private View view;
    private RadioGroup rgrpServicio;
    private RadioButton rbtnElect;
    private RadioButton rbtnAgua;
    private Spinner spinnerEstadist;
    private Button btnConsEstadist;
    private TextView txtVValorProm;
    private TextView txtVFechaProm;
    private TextView txtVValorMax;
    private TextView txtVFechaMax;
    private TextView txtVValorMin;
    private TextView txtVFechaMin;
    private GraphView grafico;

    private ArrayList<String> etiquetasGraf;

    private boolean conectando = false;
    private ProgressDialog progressDialog;

    public EstadistFragment() {
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

        view = inflater.inflate(R.layout.fragment_estadist, container, false);
        rgrpServicio = (RadioGroup) view.findViewById(R.id.rgrpServicio);
        rbtnElect = (RadioButton) view.findViewById(R.id.rbtnElect);
        rbtnAgua = (RadioButton) view.findViewById(R.id.rbtnAgua);
        spinnerEstadist = (Spinner) view.findViewById(R.id.spinnerEstadist);
        btnConsEstadist = (Button) view.findViewById(R.id.btnConsEstadist);
        txtVValorProm = (TextView) view.findViewById(R.id.txtVValorProm);
        txtVFechaProm = (TextView) view.findViewById(R.id.txtVFechaProm);
        txtVValorMax = (TextView) view.findViewById(R.id.txtVValorMax);
        txtVFechaMax = (TextView) view.findViewById(R.id.txtVFechaMax);
        txtVValorMin = (TextView) view.findViewById(R.id.txtVValorMin);
        txtVFechaMin = (TextView) view.findViewById(R.id.txtVFechaMin);
        grafico = (GraphView) view.findViewById(R.id.graph);

        ArrayAdapter<TipoEstadistica> adapter = new ArrayAdapter<>(this.getActivity(),
                R.layout.support_simple_spinner_dropdown_item, TipoEstadistica.values());
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerEstadist.setAdapter(adapter);

        btnConsEstadist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarEstadisticas();
            }
        });

        grafico.getGridLabelRenderer().setHorizontalLabelsAngle(90);

        return view;
    }

    private void consultarEstadisticas() {
        Calendar cal;
        TipoConsumo tipoServicio;
        TipoEstadistica tipoEstadistica = null;
        Timestamp fechaHoy;
        int idArduino;
        String[] posiblesEtiq;
        //String[] etiquetasGraf = new String[]{};
        etiquetasGraf = new ArrayList<String>();

        if(conectando)
            return;

        if(rgrpServicio.getCheckedRadioButtonId() == -1){
            Toast.makeText(getActivity(), R.string.error_selecc_servicio, Toast.LENGTH_SHORT).show();
            return;
        }
        if(spinnerEstadist.getSelectedItem() == null){
            Toast.makeText(getActivity(), R.string.error_selecc_tipo_est, Toast.LENGTH_SHORT).show();
            return;
        }

        if(rbtnElect.isChecked()) {
            tipoServicio = TipoConsumo.ELECTRICIDAD;
        }
        else {
            tipoServicio = TipoConsumo.AGUA;
        }

        cal = Calendar.getInstance();

        fechaHoy = Utils.timestampServer(cal.getTime());

        switch ((TipoEstadistica) spinnerEstadist.getSelectedItem()){
            case POR_MES:
                tipoEstadistica = TipoEstadistica.POR_MES;
                posiblesEtiq = getResources().getStringArray(R.array.string_array_meses);
                Integer mes = cal.get(Calendar.MONTH);
                Integer mesAux;

                for (int i = 0; i < 12; i++) {    //lleno las etiquetas de los meses
                    mesAux = mes - 11 + (2 * i);
                    if (mesAux >= 0) {
                        etiquetasGraf.add(i, posiblesEtiq[mesAux]);
                    } else {
                        etiquetasGraf.add(i, posiblesEtiq[mesAux + 12]);
                    }
                    mes = mes - 1;
                }
                break;

            case POR_DIA:
                tipoEstadistica = TipoEstadistica.POR_DIA;
                posiblesEtiq = getResources().getStringArray(R.array.string_array_dias);
                Integer dia = cal.get(Calendar.DAY_OF_WEEK)-1;  //Resto uno porque por ej el 3er dia de la semana en el array seria la posicion 2
                Integer diaAux;

                for (int i = 0; i < posiblesEtiq.length; i++) {    //lleno las etiquetas de los días
                    diaAux = dia-6+(2*i);
                    if (diaAux >= 0) {
                        etiquetasGraf.add(i, posiblesEtiq[diaAux]);
                    }
                    else {
                        etiquetasGraf.add(i, posiblesEtiq[diaAux+7]);
                    }
                    dia = dia - 1;
                }
                break;

            case POR_HORA:
                tipoEstadistica = TipoEstadistica.POR_HORA;
                posiblesEtiq = getResources().getStringArray(R.array.string_array_horas);
                Integer hora = cal.get(Calendar.HOUR_OF_DAY);
                Integer horaAux;

                for (int i = 0; i < 6; i++) {    //lleno las etiquetas de las horas
                    horaAux = hora - 5 + (2 * i);
                    if (horaAux >= 0) {
                        etiquetasGraf.add(i, posiblesEtiq[horaAux]);
                    } else {
                        etiquetasGraf.add(i, posiblesEtiq[horaAux + 24]);
                    }
                    hora = hora - 1;
                }
                break;
            default:
                tipoEstadistica = TipoEstadistica.POR_MES;
                break;
        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //había que ponerle un valor default, puse que sea -1
        idArduino = prefs.getInt(getString(R.string.pref_id_arduino), -1);

        if(idArduino != -1){
            if(Utils.conexionAInternetOk(getActivity())){
                String url = ConstructorUrls.estadisticas(idArduino, tipoServicio, tipoEstadistica, fechaHoy);
                new TaskRequestUrl(this).execute(url, "GET");
            } else{
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getActivity(), getString(R.string.error_no_arduino), Toast.LENGTH_SHORT).show();
        }
    }

    private void completarEstadisticas(ArrayList<Double> valores) throws JSONException {
        // Limpia datos anteriores
        grafico.removeAllSeries();
        txtVValorMax.setText("");
        txtVValorMin.setText("");
        txtVValorProm.setText("");
        txtVFechaMax.setText("");
        txtVFechaMin.setText("");

        // Puntos para el grafico
        DataPoint[] dataPoints = new DataPoint[valores.size()];
        for(int n = 0; n < valores.size(); n++){
            DataPoint dataPoint = new DataPoint(n, valores.get(n));
            dataPoints[n] = dataPoint;
        }

        // Completa grafico
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(dataPoints);
        series.setSpacing(50);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(grafico);
        staticLabelsFormatter.setHorizontalLabels(etiquetasGraf.toArray(new String[etiquetasGraf.size()]));
        grafico.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        grafico.addSeries(series);

        // Calculo maximo, minimo y promedio
        double maximo = valores.get(0);
        int indiceMaximo = 0;
        double minimo = valores.get(0);
        int indiceMinimo = 0;
        double total = 0;
        double promedio;

        for(int n = 0; n < valores.size(); n++){
            double valor = valores.get(n);
            total = total + valor;
            if(valor > maximo){
                maximo = valor;
                indiceMaximo = n;
            }
            if(valor < minimo){
                minimo = valor;
                indiceMinimo = n;
            }
        }
        promedio = total / valores.size();
        // Completa txts max/min/prom
        DecimalFormat redondeo2Dec = new DecimalFormat("0.####");
        txtVValorMax.setText(redondeo2Dec.format(maximo) + " KWh");
        txtVValorMin.setText(redondeo2Dec.format(minimo) + " KWh");
        txtVValorProm.setText(redondeo2Dec.format(promedio) + " KWh");
        txtVFechaMax.setText(etiquetasGraf.get(indiceMaximo));
        txtVFechaMin.setText(etiquetasGraf.get(indiceMinimo));
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
                    JSONObject data = json.getJSONObject("data");
                    JSONArray jsonArray = data.toJSONArray(data.names());

                    ArrayList<Double> valores = new ArrayList<>();
                    for(int n = 0; n < jsonArray.length(); n++){
                        valores.add(jsonArray.getDouble(n));
                    }

                    completarEstadisticas(valores);
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
