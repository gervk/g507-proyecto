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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import g507.controldeconsumo.modelo.Periodo;

public class ConsAcumFragment extends Fragment {

    private View view;
    private RadioGroup rgrpServicio;
    private RadioButton rbtnElect;
    private RadioButton rbtnAgua;
    private Spinner spinPeriodo;
    private Button btnConsultar;
    private Calendar cal;
    private String fechaIni;
    private String fechaFin;
    private Integer tipoServicio;
    private Integer idArduino;

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
        /*else {
            if(rbtnElect.isChecked()) {
                tipoServicio = 1;
            }
            else {
                tipoServicio = 2;
            }
        }*/

        if(spinPeriodo.getSelectedItem() == null){
            Toast.makeText(getActivity(), R.string.error_selecc_periodo, Toast.LENGTH_SHORT).show();
            return;
        }

        /*cal = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        fechaFin = dateFormat.format(cal.getTime());

        if(spinPeriodo.getSelectedItem().toString() == "Dia") {
            cal.add(Calendar.DATE, -1);
            fechaIni = dateFormat.format(cal.getTime());
        }
        else {
            if(spinPeriodo.getSelectedItem().toString() == "Semana") {
                cal.add(Calendar.DATE, -7);
                fechaIni = dateFormat.format(cal.getTime());
            }
            else {
                if(spinPeriodo.getSelectedItem().toString() == "Mes") {
                    cal.add(Calendar.MONTH, -1);
                    fechaIni = dateFormat.format(cal.getTime());
                }
                else {
                    if(spinPeriodo.getSelectedItem().toString() == "Bimestre") {
                        cal.add(Calendar.MONTH, -2);
                        fechaIni = dateFormat.format(cal.getTime());
                    }
                }
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //había que ponerle un valor default, puse que sea -1
        idArduino = Integer.parseInt(prefs.getString(getString(R.string.pref_id_arduino), "-1"));
        */

        //TODO consulta a la base con estos campos: fechaIni, fechaFin, tipoConsumo, idArduino
        Toast.makeText(getActivity(), R.string.error_servidor_no_disp, Toast.LENGTH_SHORT).show();
    }

}
