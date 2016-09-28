package g507.controldeconsumo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.conexion.TaskGetUrl;
import g507.controldeconsumo.conexion.TaskListener;

public class ConsActualFragment extends Fragment implements TaskListener{

    private View view;
    private RadioGroup rgrpServicio;
    private Button btnConsultar;
    private TextView txtVResulActual;

    private ProgressDialog progressDialog;
    private boolean descargandoDatos = false;

    public ConsActualFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Para que mantenga la instancia del fragment ante una recreacion del activity
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Vuelve a mostrar el msj de espera si en el estado anterior estaba descargando datos
        if(descargandoDatos)
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cons_actual, container, false);
        rgrpServicio = (RadioGroup) view.findViewById(R.id.rgrpServicio);
        btnConsultar = (Button) view.findViewById(R.id.btnConsultarActual);
        txtVResulActual = (TextView) view.findViewById(R.id.txtVResulActual);

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultar();
            }
        });

        return view;
    }

    private void consultar(){
        //Si ya esta descargando datos, no hace nada
        if(descargandoDatos)
            return;

        if(rgrpServicio.getCheckedRadioButtonId() == -1){
            Toast.makeText(getActivity(), R.string.error_selecc_servicio, Toast.LENGTH_SHORT).show();
        } else{
            if(!Utils.conexionAInternetOk(getActivity())){
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            } else{
                new TaskGetUrl(this).execute(ConstructorUrls.consumoActual(1000020009));
            }
        }
    }

    @Override
    public void onDetach() {
        //Cierra el progressDialog si se saca el fragment del activity
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDetach();
    }

    @Override
    public void inicioTask() {
        descargandoDatos = true;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finTaskGetUrl(JSONObject json) {
        descargandoDatos = false;

        if(json != null){
            try {
                txtVResulActual.setText(String.valueOf(json.getJSONArray("data").getJSONObject(0).getDouble("consumo")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_consulta_serv) , Toast.LENGTH_SHORT).show();
        }

        if(progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void finTaskPost(boolean postOk) {

    }

}
