package g507.controldeconsumo.conexion;

import android.net.Uri;

/**
 * Genera las URL necesarias para las consultas/guardado de datos
 * Al estar todas aca es mas facil corregirlas
 */
public class ConstructorUrls {
    private static final String PROTOCOLO = "https";
    private static final String URL_BASE = "enersaving-laravel.herokuapp.com";
    private static final String PATH_API = "api";
    private static final String PATH_VERSION = "v1";

    public static String consumoActual(Integer codArduino){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .authority(URL_BASE)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("consumo")
                .appendQueryParameter("id", String.valueOf(codArduino));

        return builder.build().toString();
    }

    public static String login(String usuario, String password){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .authority(URL_BASE)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath("autenticar")
                .appendQueryParameter("username", usuario)
                .appendQueryParameter("pass", password);

        return builder.build().toString();
    }
}
