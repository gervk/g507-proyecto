package g507.controldeconsumo;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import 	java.util.concurrent.TimeUnit;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.conexion.Utils;
import g507.controldeconsumo.modelo.EmpresaElec;
import g507.controldeconsumo.modelo.ServicioAgua;
import g507.controldeconsumo.modelo.ServicioElectricidad;
import g507.controldeconsumo.modelo.TipoConsumo;

public class ProxFacFragment extends Fragment implements TaskListener {

    private View view;
    private RadioGroup rgrpServicio;
    private Button btnCalcular;
    private TextView txtVConsumoMes;
    private TextView txtVCostoProxFac;
    private boolean consultandoFactura = false;
    private boolean obteniendoAcum = false;
    private boolean primerConsumo = false;
    private ServicioAgua servicioAgua;
    private ServicioElectricidad servicioElectricidad;
    private Double consumo;
    private Long diferencia;
    private Integer idArduino;
    private Integer idEmpresa;
    private TipoConsumo tipoServicio;
    private boolean obteniendoTarifa = false;
    private Calendar cal;
    private String fechaIni;
    private String fechaFin;
    private ProgressDialog progressDialog;
    private Double consumoActual;
    private Integer dias;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Integer idUsuario = prefs.getInt(getString(R.string.pref_sesion_inic), -1);
        idEmpresa = prefs.getInt(getString(R.string.pref_empresa_elect), -1);

        switch(rgrpServicio.getCheckedRadioButtonId()){
            case -1:
                Toast.makeText(getActivity(), R.string.error_selecc_servicio, Toast.LENGTH_SHORT).show();
                break;
            case R.id.rbtnElect :
                tipoServicio =  TipoConsumo.ELECTRICIDAD;
                if(idEmpresa == -1){
                    Toast.makeText(getActivity(), getString(R.string.error_complete_config), Toast.LENGTH_SHORT).show();
                }else{
                    obtenerDatosFactura(idUsuario, tipoServicio);
                }
                break;
            case R.id.rbtnAgua:
                tipoServicio = TipoConsumo.AGUA;
                obtenerDatosFactura(idUsuario, tipoServicio);
                break;
        }
    }

    private void obtenerDatosFactura(Integer idUsuario, TipoConsumo tipoServicio){
            if(Utils.conexionAInternetOk(getActivity())){
                consultandoFactura =true;
                new TaskRequestUrl(this).execute(ConstructorUrls.factura(idUsuario, tipoServicio), "GET");
            } else {
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            }

    }

    private Double obtenerDiferencia(String fecha, TipoConsumo tipoConsumo){
        tipoServicio = tipoConsumo;
        cal = Calendar.getInstance();
        fechaIni = fecha;
        fechaFin = dateFormat.format(cal.getTime());
        diferenciaDeDias(fechaIni, fechaFin);

        if (diferencia > 61){
            Integer bimestresPasados = (int)(diferencia/61);
            Integer dias = (int)(diferencia - 61*bimestresPasados);
            cal.add(Calendar.DATE, -dias);
            fechaIni = dateFormat.format(cal.getTime());
            diferenciaDeDias(fechaIni, fechaFin);
        }

        obtenerConsumo(fechaIni,fechaFin,tipoServicio);
        primerConsumo = false;
        return consumo;
    }

    private void obtenerConsumo(String fechaIni, String fechaFin, TipoConsumo tipoConsumo) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        idArduino = prefs.getInt(getString(R.string.pref_id_arduino), -1);

        if(idArduino != -1){
            if(Utils.conexionAInternetOk(getActivity())){
                String url = ConstructorUrls.consumoAcumulado(idArduino, tipoConsumo,
                        Timestamp.valueOf(fechaIni), Timestamp.valueOf(fechaFin));
                consultandoFactura = false;
                obteniendoAcum = true;
                new TaskRequestUrl(this).execute(url, "GET");
            } else {
                Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_no_arduino), Toast.LENGTH_SHORT).show();
        }

    }

    private void obtenerTarifas(Double consumo){
        obteniendoTarifa = true;
        EmpresaElec empresaElec = EmpresaElec.getEmpresaById(idEmpresa);
        //EmpresaElec empresaElec = EmpresaElec.getEmpresaById(2);
        servicioElectricidad.setEmpresa(empresaElec);

        if(Utils.conexionAInternetOk(getActivity())){
            consultandoFactura =false;
            obteniendoAcum = false;
            new TaskRequestUrl(this).execute(ConstructorUrls.tarifa(consumo, empresaElec.getId()), "GET");
        } else {
            Toast.makeText(getActivity(), R.string.error_internet_no_disp, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void inicioRequest() {
        if(progressDialog == null){
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
        }
        if(progressDialog!= null && !progressDialog.isShowing())
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {
        if(progressDialog != null)
            progressDialog.dismiss();
        if (consultandoFactura){
            consultandoFactura =false;
            procesarJsonConsultaFactura(json);
        }else{
            if(obteniendoAcum){
                procesarJsonAcum(json);
                obteniendoAcum =false;
            }else{
               if(obteniendoTarifa){
                procesarJsonTarifa(json);
                obteniendoTarifa = false;
            }
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

    private void calcularAgua(){
        Double totalAPagar = servicioAgua.calcularCosto(diferencia, consumo);
        if(totalAPagar < 271.00){
            totalAPagar = 271.00;
        }
        txtVConsumoMes.setText(new DecimalFormat("0.##").format(consumo)+" m3");
        txtVCostoProxFac.setText(new DecimalFormat("0.##").format(totalAPagar)+" $");
    }

    private void calcularElectricidad(){
        consumoActual = consumo;
        dias = diferencia.intValue();

        Double totalAPagar;

        diferenciaDeDias(servicioElectricidad.getFecPrimerConsumo(), servicioElectricidad.getFecUltFact());
        if(diferencia >= 365){
                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = cal.getTime();
                cal.setTime(date);
                cal.add(Calendar.YEAR, -1);
                fechaIni = dateFormat.format(cal.getTime());
                cal.add(Calendar.DATE, 61);
                fechaFin = dateFormat.format(cal.getTime());
                primerConsumo = true;
                obtenerConsumo(fechaIni, fechaFin, tipoServicio);
        }else{
            consumo = -1.00;
            Toast.makeText(getActivity(), getString(R.string.msj_advertencia) , Toast.LENGTH_LONG).show();
            totalAPagar = servicioElectricidad.calcularCosto(consumoActual,consumo,dias);
            txtVConsumoMes.setText(new DecimalFormat("0.##").format(consumoActual)+" KWh");
            txtVCostoProxFac.setText(new DecimalFormat("0.##").format(totalAPagar)+" $");
        }

    }

    private void procesarJsonConsultaFactura(JSONObject json){
        if(json != null){
        try {
            if(json.getString("status").equals("ok")){
                if(tipoServicio == TipoConsumo.AGUA){
                    JSONObject data = json.getJSONObject("data");
                    JSONObject servicio = data.getJSONObject("servicio");

                    servicioAgua = new ServicioAgua(servicio.getInt("id"), servicio.getDouble("k"),servicio.getDouble("zf"),
                           servicio.getDouble("tgdf"), servicio.getDouble("sc"),servicio.getDouble("ef"), servicio.getDouble("st"),
                            servicio.getDouble("aud"), servicio.getInt("fs"),servicio.getInt("cl"));
                    servicioAgua.setFecFact(servicio.getString("ultima_factura"));
                    obtenerDiferencia(servicioAgua.getFecFact()+" 00:00:00", tipoServicio);

                }else{
                    JSONObject data = json.getJSONObject("data");
                    JSONObject servicio = data.getJSONObject("servicio");
                    servicioElectricidad = new ServicioElectricidad();
                    servicioElectricidad.setFecUltFact(servicio.getString("ultimaFactura")+ " 00:00:00");
                    if(servicio.isNull("primerConsumo")){
                        servicioElectricidad.setFecPrimerConsumo(dateFormat.format(new Date().getTime()));
                    }else{
                        JSONObject primerConsumo = servicio.getJSONObject("primerConsumo");
                        //servicioElectricidad.setPrimerConsumo(primerConsumo.getDouble("consumo"));
                        servicioElectricidad.setFecPrimerConsumo(primerConsumo.getString("updated_at"));
                    }

                    //servicioElectricidad.setFecPrimerConsumo("2015-05-20 00:00:00");
                    obtenerDiferencia(servicioElectricidad.getFecUltFact(), tipoServicio);
                }

            } else if(json.getString("status").equals("error")){
                Toast.makeText(getActivity(), getString(R.string.error_complete_config) , Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.error_traducc_datos) , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    } else {
        Toast.makeText(getActivity(), getString(R.string.error_inesperado_serv) , Toast.LENGTH_SHORT).show();
    }
    }

    private void procesarJsonAcum(JSONObject json){
        obteniendoAcum = false;
        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    consumo = json.getDouble("data");
                    if(tipoServicio == TipoConsumo.AGUA){
                        calcularAgua();
                    }else {
                        if (!primerConsumo){
                        obtenerTarifas(consumo);
                        }else{
                            Double totalAPagar = servicioElectricidad.calcularCosto(consumoActual,consumo,dias);
                            txtVConsumoMes.setText(new DecimalFormat("0.##").format(consumoActual)+" KWh");
                            txtVCostoProxFac.setText(new DecimalFormat("0.##").format(totalAPagar)+" $");
                        }
                    }
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

    private void procesarJsonTarifa(JSONObject json){
        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    JSONObject data = json.getJSONObject("data");

                    servicioElectricidad.setCargo_fijo(data.getDouble("cargo_fijo"));
                    servicioElectricidad.setCargo_variable(data.getDouble("cargo_variable"));
                    servicioElectricidad.setA_10_20_fijo(data.getDouble("a_10_20_fijo"));
                    servicioElectricidad.setA_10_20_variable(data.getDouble("a_10_20_variable"));
                    servicioElectricidad.setA_mas_20_fijo(data.getDouble("a_mas_20_fijo"));
                    servicioElectricidad.setA_mas_20_variable(data.getDouble("a_mas_20_variable"));

                    calcularElectricidad();
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

    private void diferenciaDeDias(String fechaIni, String fechaFin){
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = dateFormat.parse(fechaIni);
            Date date2 = dateFormat.parse(fechaFin);
            diferencia = (date2.getTime() - date1.getTime())/ (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

