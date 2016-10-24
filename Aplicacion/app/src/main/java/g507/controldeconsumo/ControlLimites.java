package g507.controldeconsumo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;
import g507.controldeconsumo.modelo.TipoConsumo;

public class ControlLimites extends BroadcastReceiver implements TaskListener{
    // Para los argumentos recibidos en el intent
    public static final String ARG_LIMITE = "argLimite";
    public static final String ARG_TIPO = "argTipo";
    public static final String ARG_COD_ARDUINO = "argCodArduino";

    // Para el intent del mainActivity cuando se clickea una notif
    public static final String ARG_TIPO_NOTIF = "argTipoNotif";

    private TipoConsumo tipoConsumo;
    private int limite;
    private int codArduino;

    private Context context;

    private boolean conectando = false;

    public ControlLimites() {
    }

    /**
     * Metodo llamado por la alarma cada determinado tiempo,
     * consulta el consumo acumulado del mes del servicio limitado y
     * muestra notificaciones si se sobrepasa el limite
     * @param context
     * @param intent debe tener el valor del limite, del tipo de consumo y el cod arduino
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        tipoConsumo = TipoConsumo.getById(intent.getIntExtra(ARG_TIPO, -1));
        limite = intent.getIntExtra(ARG_LIMITE, -1);
        codArduino = intent.getIntExtra(ARG_COD_ARDUINO, -1);

        // Fecha inicio = primer dia del mes; Fecha fin = fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Timestamp fechaFin = Timestamp.valueOf(dateFormat.format(cal.getTime()));
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Timestamp fechaIni = Timestamp.valueOf(dateFormat.format(cal.getTime()));

        if(limite != -1){
            new TaskRequestUrl(this).execute(ConstructorUrls.consumoAcumulado(codArduino, tipoConsumo,
                    fechaIni, fechaFin), "GET");
        }
    }

    private void mostrarNotificacion(Context context, double consumo) {
        NotificationManager notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Intent a ejecutar al clickear la notificacion
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra(ARG_TIPO_NOTIF, tipoConsumo.getId());
        // Usa el tiempo para tener un id unico
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), mainIntent, 0);

        String consumoRedondeado = new DecimalFormat("0.##").format(consumo);

        Notification notif = new Notification.Builder(context)
                .setContentTitle("Alerta límite de consumo")
                .setContentText("Consumo " + tipoConsumo + " :" + consumoRedondeado)
                .setSmallIcon(R.drawable.enersaving) //fixme arreglar icono
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();

        notifManager.notify(0, notif);
    }

    @Override
    public void inicioRequest() {
        //no implementar
    }

    @Override
    public void finRequest(JSONObject json) {
        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    double consumo = json.getDouble("data");
                    if(consumo > limite){
                        mostrarNotificacion(context, consumo);
                    }
                } else if(json.getString("status").equals("error")){
                    Log.e("ControlLimites", "Datos incorrectos consulta " + tipoConsumo);
                }
            } catch (JSONException e) {
                Log.e("ControlLimites", "Error al traducir datos consulta " + tipoConsumo);
                e.printStackTrace();
            }
        } else {
            Log.e("ControlLimites", "Error inesperado servidor consulta " + tipoConsumo);
        }
    }

}
