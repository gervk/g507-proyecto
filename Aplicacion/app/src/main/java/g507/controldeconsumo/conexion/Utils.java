package g507.controldeconsumo.conexion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import g507.controldeconsumo.R;

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    private static final SimpleDateFormat dateFormatServer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Devuelve true o false segun si tiene conexion a internet
     * @param activity
     * @return true/false
     */
    public static boolean conexionAInternetOk(Activity activity) {
        ConnectivityManager connMgr = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Hace un request sobre la url dada
     * @param direccUrl
     * @param metodo "GET" / "POST"
     * @return
     */
    public static String requestUrl(String direccUrl, String metodo) {
        String respuesta;
        URL url;
        HttpURLConnection conexionUrl = null;

        try {
            url = new URL(direccUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error al formar la URL " + direccUrl, e);
            e.printStackTrace();
            return null;
        }

        try {
            conexionUrl = (HttpURLConnection) url.openConnection();
            conexionUrl.setRequestMethod(metodo);
            conexionUrl.connect();

            InputStream inputStream = conexionUrl.getInputStream();
            respuesta = parsearAString(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conexionUrl != null) {
                conexionUrl.disconnect();
            }
        }

        return respuesta;
    }

    /**
     * Parsea a string un stream devuelto por una URL
     * @param inputStream
     * @return
     */
    public static String parsearAString(InputStream inputStream){
        if (inputStream == null) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String linea;
        try {
            while ((linea = reader.readLine()) != null) {
                buffer.append(linea).append("\n");
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error parseando el stream a string", e);
            e.printStackTrace();
        }

        if (buffer.length() == 0) {
            // Stream vacio
            return null;
        }

        if (reader != null) {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e(LOG_TAG, "Error cerrando el stream", e);
            }
        }

        return buffer.toString();
    }

    public static JSONObject parsearAJson(String texto){
        try {
            JSONObject json = new JSONObject(texto);
            return json;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error al parsear a JSON", e);
            e.printStackTrace();
        }

        return null;
    }

    public static Timestamp timestampServer(Date date){
        return Timestamp.valueOf(dateFormatServer.format(date));
    }

    public static void configDireccServer(SharedPreferences sharedPreferences, String keyPrefServerLocal, String keyPrefIpServer){
        if(!sharedPreferences.getBoolean(keyPrefServerLocal, false)){
            // Si no se usa servidor local, setea url base como la de heroku
            ConstructorUrls.urlBase = ConstructorUrls.URL_BASE_CLOUD;
            Log.d("Settings", "Direccion server: " + ConstructorUrls.urlBase);
        } else{
            // Si usa servidor local seta url base segun la ip configurada
            String ipServer = sharedPreferences.getString(keyPrefIpServer, "");
            if(!ipServer.equals("")){
                ConstructorUrls.urlBase = ipServer;
            }
            Log.d("Settings", "Direccion server: " + ConstructorUrls.urlBase);
        }
    }

    public static boolean alfanumericoSinEspacios(String texto){
        return texto.matches("\\w*") && texto.matches("[A-Za-z0-9][^.]*");
    }
}