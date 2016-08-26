package g507.controldeconsumo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ProxFacFragment extends Fragment {

    private View view;
    private RadioGroup rgrpServicio;
    private Button btnCalcular;
    private TextView txtVConsumoMes;
    private TextView txtVCostoProxFac;

    public ProxFacFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_prox_fac, container, false);
        rgrpServicio = (RadioGroup) view.findViewById(R.id.rgrpServicio);
        btnCalcular = (Button) view.findViewById(R.id.btnCalcular);
        txtVConsumoMes = (TextView) view.findViewById(R.id.txtVConsumoMes);
        txtVCostoProxFac = (TextView) view.findViewById(R.id.txtVCostoProxFac);

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcular();
            }
        });

        return view;
    }

    private void calcular(){
        switch(rgrpServicio.getCheckedRadioButtonId()){
            case -1:
                Toast.makeText(getActivity(), R.string.error_selecc_servicio, Toast.LENGTH_SHORT).show();
                break;
            case R.id.rbtnLuz:
                //TODO calcular factura electricidad
                break;
            case R.id.rbtnAgua:
                //TODO calcular factura agua;
                break;
        }
    }
}
