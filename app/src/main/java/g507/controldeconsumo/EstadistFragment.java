package g507.controldeconsumo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EstadistFragment extends Fragment {

    public EstadistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_estadist, container, false);
        View v = inflater.inflate(R.layout.fragment_estadist, container, false);

        Spinner dropdown = (Spinner)v.findViewById(R.id.spinnerEstadist);
        String[] items = new String[]{"Por hora", "Por día", "Por mes"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),R.layout.support_simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        return v;
        /*
        View v = inflater.inflate(R.layout.fragment_cons_acum, container, false);

        Spinner dropdown = (Spinner)v.findViewById(R.id.spinnerConsAcum);
        String[] items = new String[]{"Día", "Semana", "Mes", "Bimestre"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        return v;

         */

    }

}
