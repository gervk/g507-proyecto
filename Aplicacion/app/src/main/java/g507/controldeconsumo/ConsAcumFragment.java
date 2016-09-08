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
import android.widget.Toast;

import g507.controldeconsumo.modelo.Periodo;

public class ConsAcumFragment extends Fragment {

    private View view;
    private RadioGroup rgrpServicio;
    private Spinner spinPeriodo;
    private Button btnConsultar;

    public ConsAcumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cons_acum, container, false);
        rgrpServicio = (RadioGroup) view.findViewById(R.id.rgrpServicio);
        spinPeriodo = (Spinner) view.findViewById(R.id.spinPeriodo);
        btnConsultar = (Button) view.findViewById(R.id.btnConsAcum);

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
        if(rgrpServicio.getCheckedRadioButtonId() == -1){
            Toast.makeText(getActivity(), R.string.error_selecc_servicio, Toast.LENGTH_SHORT).show();
            return;
        }
        if(spinPeriodo.getSelectedItem() == null){
            Toast.makeText(getActivity(), R.string.error_selecc_periodo, Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO consultar
        Toast.makeText(getActivity(), R.string.error_servidor_no_disp, Toast.LENGTH_SHORT).show();
    }

}
