package g507.controldeconsumo;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.modelo.TipoConsumo;
import g507.controldeconsumo.modelo.TipoEstadistica;

public class EstadistFragment extends Fragment {
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

    public EstadistFragment() {
        // Required empty public constructor
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

        return view;
    }

    private void consultarEstadisticas() {

        Calendar cal;
        TipoConsumo tipoServicio;
        TipoEstadistica tipoEstadistica;
        String fechaHoy;
        int idArduino;
        String[] posiblesEtiq;
        //String[] etiquetasGraf = new String[]{};
        ArrayList<String> etiquetasGraf = new ArrayList<String>();

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fechaHoy = dateFormat.format(cal.getTime());

        switch ((TipoEstadistica) spinnerEstadist.getSelectedItem()){
            case POR_MES:
                tipoEstadistica = TipoEstadistica.POR_MES;
                posiblesEtiq = getResources().getStringArray(R.array.string_array_meses);
                Integer mes = cal.get(Calendar.MONTH);
                Integer mesAux;

                for (int i = 0; i < 6; i++) {    //lleno las etiquetas de los días
                    mesAux = mes - 5 + (2 * i);
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

                for (int i = 0; i < 6; i++) {    //lleno las etiquetas de los días
                    horaAux = hora - 5 + (2 * i);
                    if (horaAux >= 0) {
                        etiquetasGraf.add(i, posiblesEtiq[horaAux]);
                    } else {
                        etiquetasGraf.add(i, posiblesEtiq[horaAux + 24]);
                    }
                    hora = hora - 1;
                }
                break;
        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //había que ponerle un valor default, puse que sea -1
        idArduino = prefs.getInt(getString(R.string.pref_id_arduino), -1);

        /*
        if(idArduino != -1){
            if(Utils.conexionAInternetOk(getActivity())){
                String url = ConstructorUrls.consumoAcumulado(idArduino, tipoEstadistica,
                        Timestamp.valueOf(fechaHoy));
                new TaskRequestUrl(this).execute(url, "GET");
            } else{
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getActivity(), "No hay un arduino asociado", Toast.LENGTH_SHORT).show();
        }
        */

        //TODO consultar
        Toast.makeText(getActivity(), R.string.error_servidor_no_disp, Toast.LENGTH_SHORT).show();

        mockGrafico(etiquetasGraf);
    }

    private void mockGrafico(ArrayList<String> etiquetas) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 170),
                new DataPoint(1, 156),
                new DataPoint(2, 137),
                new DataPoint(3, 180),
                new DataPoint(4, 164),
                new DataPoint(5, 220),
                new DataPoint(6, 205)
        });

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(grafico);
        staticLabelsFormatter.setHorizontalLabels(etiquetas.toArray(new String[etiquetas.size()]));
        grafico.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        grafico.addSeries(series);

    }

}
