package g507.controldeconsumo.conexion;

import android.net.Uri;

import java.sql.Time;
import java.sql.Timestamp;

import g507.controldeconsumo.modelo.PreguntaSeguridad;
import g507.controldeconsumo.modelo.TipoConsumo;

/**
 * Genera las URL necesarias para las consultas/guardado de datos
 * Al estar todas aca es mas facil corregirlas
 */
public class ConstructorUrls {
    private static final String PROTOCOLO = "https";
    private static final String URL_BASE = "enersaving-laravel.herokuapp.com";
    private static final String PATH_API = "api";
    private static final String PATH_VERSION = "v1";

    public static String consumoActual(Integer codArduino, TipoConsumo tipoConsumo){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .authority(URL_BASE)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("consumo")
                .appendPath(String.valueOf(codArduino))
                .appendQueryParameter("tipo", String.valueOf(tipoConsumo.getId()));

        return builder.build().toString();
    }

    public static String consumoAcumulado(Integer codArduino, TipoConsumo tipoConsumo, Timestamp fechaIni, Timestamp fechaFin){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .authority(URL_BASE)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("consumo")
                .appendPath(String.valueOf(codArduino))
                .appendPath("acumulado")
                .appendQueryParameter("tipo", String.valueOf(tipoConsumo.getId()))
                .appendQueryParameter("inicio", String.valueOf(fechaIni.getTime()))
                .appendQueryParameter("desde", String.valueOf(fechaFin.getTime()));

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

    public static String registro(String usuario, String pass, String mail, PreguntaSeguridad pregSeg, int respPreg){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .authority(URL_BASE)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendQueryParameter("username", usuario)
                .appendQueryParameter("pass", pass)
                .appendQueryParameter("mail", mail)
                .appendQueryParameter("idPregSeg", String.valueOf(pregSeg.getId()))
                .appendQueryParameter("respPreg", String.valueOf(respPreg));

        return builder.build().toString();
    }

    public static String asociarArduino(Integer idUsuario, Integer idArduino){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .authority(URL_BASE)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath("regArduino")
                .appendQueryParameter("id", String.valueOf(idUsuario))
                .appendQueryParameter("arduino", String.valueOf(idArduino));

        return builder.build().toString();
    }

    public static String cambiarContraseña(Integer idUsuario, String pass) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .authority(URL_BASE)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(String.valueOf(idUsuario))
                .appendQueryParameter("newPassword", pass);

        return builder.build().toString();
    }

    public static String getUsuario(String username){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .authority(URL_BASE)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(username)
                .appendQueryParameter("username", "1");

        return builder.build().toString();
    }

    public static String getUsuario(Integer idUsuario){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .authority(URL_BASE)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(String.valueOf(idUsuario));

        return builder.build().toString();
    }
}
