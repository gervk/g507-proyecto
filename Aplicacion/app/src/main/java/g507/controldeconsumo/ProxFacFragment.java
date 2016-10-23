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
import g507.controldeconsumo.modelo.TipoConsumo;

public class ProxFacFragment extends Fragment implements TaskListener {

    private View view;
    private RadioGroup rgrpServicio;
    private Button btnCalcular;
    private TextView txtVConsumoMes;
    private TextView txtVCostoProxFac;
    private boolean conectando = false;
    //valores hardcodeados para poder probar
    private String fecUltFact = "2016-05-13 17:06:48";
    private String fecPrimerConsumo = "2015-08-23 17:06:48";
    private Integer k = 1;
    private Integer zf =2;
    private Integer tgdf =3;
    private Integer sc =4;
    private Integer ef = 5;
    private Integer st = 6;
    private Integer aud = 7;
    private Integer fs = 1;//solo puede tener valor 1 o 2
    private Integer cl = 400;
    private Integer consumo = 500;
    private Long diferencia;

    private ProgressDialog progressDialog;

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
        TipoConsumo tipoServicio;
        Integer idUsuario = 0;
        //asignacion idUsuario

        switch(rgrpServicio.getCheckedRadioButtonId()){
            case -1:
                Toast.makeText(getActivity(), R.string.error_selecc_servicio, Toast.LENGTH_SHORT).show();
                break;
            case R.id.rbtnElect :
                facturaElectricidad(idUsuario, TipoConsumo.ELECTRICIDAD);
                break;
            case R.id.rbtnAgua:
                facturaAgua(idUsuario, TipoConsumo.AGUA);
                break;
        }
    }

    private void facturaAgua(Integer idUsuario, TipoConsumo tipoServicio){
        //pido las constantes de la config del servicio de agua
        // TODO  para un id de usuario y tipo de consumo -> devuelve un array con K, Zf, TGDf, SC, Ef, ST, AUD, FS, CL, fecha de última factura de agua
        //new TaskRequestUrl(this).execute(ConstructorUrls.factura(idUsuario, tipoServicio), "GET");

        /*
        if(no encuentra los valores){
            mensaje para pedirle que complete la configuración de servicio de agua
        }*/
        //else{
            //pido el consumo acumulado de acuerdo a la fecha de ultima factura devuelta
            Integer consumoRegistrado = obtenerDiferencia(fecUltFact, tipoServicio);
            Double totalAPagar = logicaFacturaAgua(k, zf, tgdf, sc, ef, st, aud, fs, cl, diferencia, consumoRegistrado);
            txtVConsumoMes.setText(new DecimalFormat("0.##").format(consumoRegistrado)+" m3");
            txtVCostoProxFac.setText(new DecimalFormat("0.##").format(totalAPagar)+" $");
    }

    private Double logicaFacturaAgua(Integer k, Integer zf, Integer tgdf, Integer sc, Integer ef, Integer st, Integer aud, Integer fs, Integer cl, Long dias, Integer consumoRegistrado){

        Integer cargoFijo = (k*(zf/1000)*(sc*ef+(st/10))+(aud*k*fs)*dias.intValue());
        Double precio;
        if(fs ==1){
            precio = 0.3288;
        }else{
            precio = 0.6566;
        }
        //el precio en realiadad difiere si se tiene
        Double cargoVariable = (consumoRegistrado - cl)* precio* k*fs;

        //TODO ver impuestos que aplican

        return efectuarImpuestos(TipoConsumo.AGUA, cargoFijo+cargoVariable);
    }

    private void facturaElectricidad(Integer idUsuario, TipoConsumo tipoServicio){ //ojo el ahorro se acredita en la factura del mes siguiente
;
        //TODO pedir al servidor Para un id de usuario y tipo de consumo -> devuelve un fecha ultima factura, fecha del primer consumo registrado
        //new TaskRequestUrl(this).execute(ConstructorUrls.factura(idUsuario, tipoServicio), "GET");
        // para un usuario x pido: tipo de tarifa, empresa y fecha de ultima factura

        //fecha que devuelve el get al server
        Integer consumoActual = obtenerDiferencia(fecUltFact, tipoServicio);
        Integer dias = diferencia.intValue();
        System.out.println(dias);


        //Double totalAPagar = logicaFacturaElectricidad(dias, consumoActual, obtenerDiferencia(fecPrimerConsumo, tipoServicio));
        Double totalAPagar = logicaFacturaElectricidad(dias, consumoActual, 600);
        txtVConsumoMes.setText(new DecimalFormat("0.##").format(consumoActual)+" KWh");
        txtVCostoProxFac.setText(new DecimalFormat("0.##").format(totalAPagar)+" $");
        //mostrar en campo Costo aprox proxima factura

    }

    private Double logicaFacturaElectricidad(Integer dias, Integer consumoRegistrado, Integer consumoAnterior){

        Double totalAntesImpuestos = efectuarBonificacionesYPenalizaciones(consumoRegistrado, consumoAnterior, dias);

        return efectuarImpuestos(TipoConsumo.ELECTRICIDAD, totalAntesImpuestos);
    }

    private Double efectuarBonificacionesYPenalizaciones(Integer consumoActual,Integer consumoAnterior, Integer dias){

        //TODO Para un id usuario, tipo de consumo y consumo -> devuelve array con cargo fijo, cargo variable, cargo ahorro 1 y cargo ahorro 2 (segun tipo de tarifa)

        Double totalAntesImpuestos = 100.0;//valor asignado solo para efectuar las pruebas

        if(consumoActual < consumoAnterior*0.8){
            totalAntesImpuestos = dias*( totalAntesImpuestos - 20);
            //usar tarifa de costo fijo y variable de ahorro mayor al 20% tope de 2000 de ahorro
            //que abajo aparezca una aclaracion "durante este período el consumo fue menor que en el mismo período del año pasado"
        }else{
            if(consumoActual < consumoAnterior*0.9 ){
                totalAntesImpuestos =  dias*(totalAntesImpuestos - 10);
                //usar tarifa de costo fijo y variable de ahorro mayor al 20% tope de 2000 de ahorro
                //que abajo aparezca una aclaracion "durante este período el consumo fue menor que en el mismo período del año pasado"
            }else{
                if(consumoActual< 300){
                    if(consumoActual >= consumoAnterior*1.1){
                        //penalizacion por exceso de consumo
                        totalAntesImpuestos = dias*(totalAntesImpuestos + 20);
                        //excedente por cargo variable
                        //resto calculo normal factura
                        //que abajo aparezca una aclaracion "durante este período el consumo fue mayor que en el mismo período del año pasado"
                    }else{
                        totalAntesImpuestos = totalAntesImpuestos*dias;
                        //calculo con valores normales consumo*costo fijo y consumo*costo variable
                        //calcular para cada tipo de tarifa y empresa, teniendo en cuenta la tarifa normal (sin descuento)


                    }
                }

            }
        }

        return totalAntesImpuestos;
    }

    private Double efectuarImpuestos(TipoConsumo tipoServicio, Double valorAntesImpuestos){
        Double valorDespImpuestos = 0.0;
        if(tipoServicio == TipoConsumo.AGUA){
            //efectuar impuestos de factura de agua
            //TODO ver impuestos que aplican
            valorDespImpuestos = valorAntesImpuestos*1.21;

        }else{
            //TODO ver impuestos que aplican
            //para edesur 10% imp al serv deelectricidad, 5,5% fondo obras publicas, 21% iva, 0,6% sta cruz, 6,4% cont municipal, 0,6424 cont provincial
            valorDespImpuestos = valorAntesImpuestos*1.21;
        }

        return valorDespImpuestos;
    }


    private Integer obtenerDiferencia(String fecha, TipoConsumo tipoConsumo){
        Calendar cal;
        String fechaIni;
        String fechaFin;
        cal = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fechaFin = dateFormat.format(cal.getTime());
        fechaIni = fecha;

        try {
            Date date1 = dateFormat.parse(fechaIni);
            Date date2 = dateFormat.parse(fechaFin);
            diferencia = (date2.getTime() - date1.getTime())/ (24 * 60 * 60 * 1000);
            System.out.println ("Days: " + TimeUnit.DAYS.convert(diferencia, TimeUnit.DAYS));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (diferencia > 61){
            cal.add(Calendar.DATE, -61);
            fechaIni = dateFormat.format(cal.getTime());
            System.out.println(fechaIni);
            try {
                Date date1 = dateFormat.parse(fechaIni);
                Date date2 = dateFormat.parse(fechaFin);
                diferencia = (date2.getTime() - date1.getTime() )/ (24 * 60 * 60 * 1000);
                System.out.println ("Days: " + TimeUnit.DAYS.convert(diferencia, TimeUnit.DAYS));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        System.out.println(diferencia);
        return obtenerConsumo(fechaIni, fechaFin, tipoConsumo);

    }

    private Integer obtenerConsumo(String fechaIni, String fechaFin, TipoConsumo tipoConsumo) {

        /* getIdArduino del usuario
            String url = ConstructorUrls.consumoAcumulado(idArduino, tipoConsumo,
                    Timestamp.valueOf(fechaIni), Timestamp.valueOf(fechaFin));
            new TaskRequestUrl(this).execute(url, "GET");
            asignar respuesta a consumo*/
        return consumo;
    }

    @Override
    public void inicioRequest() {
        conectando = true;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {

        conectando = false;

        if(progressDialog != null)
            progressDialog.dismiss();

        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    JSONObject data = json.getJSONObject("data");
                    JSONArray jsonArray = data.toJSONArray(data.names());

                    ArrayList<Double> valores = new ArrayList<>();
                    for(int n = 0; n < jsonArray.length(); n++){
                        valores.add(jsonArray.getDouble(n));
                    }

                    completarCampos(valores);

                } else if(json.getString("status").equals("error")){
                    Toast.makeText(getActivity(), "Por favor complete los datos de configuracion" , Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), getString(R.string.error_traducc_datos) , Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_inesperado_serv) , Toast.LENGTH_SHORT).show();
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

    private void completarCampos(ArrayList<Double> valores){
        //llena los  field text con los valores del servidor
    }


}

