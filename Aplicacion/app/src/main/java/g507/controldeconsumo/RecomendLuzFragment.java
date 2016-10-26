package g507.controldeconsumo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendLuzFragment extends Fragment {

    private View view;
    private ListView listViewRecLuz;

    public RecomendLuzFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recomend_luz, container, false);

        listViewRecLuz = (ListView)view.findViewById(R.id.lvRecomendLuz);

        ArrayList<String> recomendLuzList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.string_array_rec_luz)));

        ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_recomend, recomendLuzList);

        listViewRecLuz.setAdapter(listAdapter);

        return view;
    }
}
