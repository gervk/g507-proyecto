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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.modelo.TipoConsumo;

public class ConsActualFragment extends Fragment implements TaskListener{

    private View view;
    private RadioGroup rgrpServicio;
    private RadioButton rbtnElect;
    private RadioButton rbtnAgua;
    private Button btnConsultar;
    private TextView txtVResulActual;
    private String unidad;
    private ProgressDialog progressDialog;
    private boolean descargandoDatos = false;

    public ConsActualFragment() {
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
        if(descargandoDatos)
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cons_actual, container, false);
        rgrpServicio = (RadioGroup) view.findViewById(R.id.rgrpServicio);
        rbtnElect = (RadioButton) view.findViewById(R.id.rbtnElect);
        rbtnAgua = (RadioButton) view.findViewById(R.id.rbtnAgua);
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
        TipoConsumo tipoConsumo;
        Integer idArduino;

        //Si ya esta descargando datos, no hace nada
        if(descargandoDatos)
            return;

        if(rgrpServicio.getCheckedRadioButtonId() == -1){
            Toast.makeText(getActivity(), R.string.error_selecc_servicio, Toast.LENGTH_SHORT).show();
        } else{
            if(!Utils.conexionAInternetOk(getActivity())){
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            } else{
                if(rbtnElect.isChecked()) {
                    tipoConsumo = TipoConsumo.ELECTRICIDAD;
                    unidad = " KWs";
                }
                else {
                    tipoConsumo = TipoConsumo.AGUA;
                    unidad = " m3";
                }
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                //había que ponerle un valor default, puse que sea -1
                idArduino = prefs.getInt(getString(R.string.pref_id_arduino), -1);

                if(idArduino != -1)
                    new TaskRequestUrl(this).execute(ConstructorUrls.consumoActual(idArduino, tipoConsumo), "GET");
                else
                    Toast.makeText(getActivity(), getString(R.string.error_no_arduino), Toast.LENGTH_SHORT).show();
            }
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

    @Override
    public void inicioRequest() {
        descargandoDatos = true;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {
        descargandoDatos = false;

        if(progressDialog != null)
            progressDialog.dismiss();

        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    double consumoPorSeg = json.getDouble("data");
                    txtVResulActual.setText(new DecimalFormat("0.####").format(consumoPorSeg)+unidad);
                } else if(json.getString("status").equals("error")){
                    //String msjError = json.getJSONArray("data").getString(0);
                    //Toast.makeText(getActivity(), msjError , Toast.LENGTH_SHORT).show();
                    txtVResulActual.setText(new DecimalFormat("0.####").format(0)+unidad);
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), getString(R.string.error_traducc_datos) , Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_inesperado_serv) , Toast.LENGTH_SHORT).show();
        }
    }

}
