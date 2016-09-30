package g507.controldeconsumo.conexion;

import android.app.Activity;
import android.content.Context;
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

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

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
                buffer.append(linea + "\n");
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
}
