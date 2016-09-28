package g507.controldeconsumo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class EstadistFragment extends Fragment {
    //Libreria para los graficos http://www.android-graphview.org/

    private View view;
    private RadioGroup rgrpServicio;
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
        spinnerEstadist = (Spinner) view.findViewById(R.id.spinnerEstadist);
        btnConsEstadist = (Button) view.findViewById(R.id.btnConsEstadist);
        txtVValorProm = (TextView) view.findViewById(R.id.txtVValorProm);
        txtVFechaProm = (TextView) view.findViewById(R.id.txtVFechaProm);
        txtVValorMax = (TextView) view.findViewById(R.id.txtVValorMax);
        txtVFechaMax = (TextView) view.findViewById(R.id.txtVFechaMax);
        txtVValorMin = (TextView) view.findViewById(R.id.txtVValorMin);
        txtVFechaMin = (TextView) view.findViewById(R.id.txtVFechaMin);
        grafico = (GraphView) view.findViewById(R.id.graph);

        String[] items = new String[]{"Por hora", "Por día", "Por mes"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(),R.layout.support_simple_spinner_dropdown_item, items);
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
        if(rgrpServicio.getCheckedRadioButtonId() == -1){
            Toast.makeText(getActivity(), R.string.error_selecc_servicio, Toast.LENGTH_SHORT).show();
            return;
        }
        if(spinnerEstadist.getSelectedItem() == null){
            Toast.makeText(getActivity(), R.string.error_selecc_tipo_est, Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO consultar
        Toast.makeText(getActivity(), R.string.error_servidor_no_disp, Toast.LENGTH_SHORT).show();

        mockGrafico();
    }

    private void mockGrafico() {
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
        staticLabelsFormatter.setHorizontalLabels(new String[] {"lun", "mar", "mie", "jue", "vie", "sab", "dom"});
        grafico.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        grafico.addSeries(series);
    }

}
