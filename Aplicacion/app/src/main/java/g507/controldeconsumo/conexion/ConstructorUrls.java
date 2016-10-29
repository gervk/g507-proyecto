package g507.controldeconsumo.conexion;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import g507.controldeconsumo.modelo.PreguntaSeguridad;
import g507.controldeconsumo.modelo.ServicioAgua;
import g507.controldeconsumo.modelo.TipoConsumo;
import g507.controldeconsumo.modelo.TipoEstadistica;

/**
 * Genera las URL necesarias para las consultas/guardado de datos
 * Al estar todas aca es mas facil corregirlas
 */
public class ConstructorUrls {

    private static final String PROTOCOLO = "https";
    public static final String URL_BASE_CLOUD = "enersaving-laravel.herokuapp.com";
    public static String urlBase = URL_BASE_CLOUD;
    private static final String PATH_API = "api";
    private static final String PATH_VERSION = "v1";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String consumoActual(Integer codArduino, TipoConsumo tipoConsumo){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("consumo")
                .appendPath(String.valueOf(codArduino))
                .appendQueryParameter("tipo", String.valueOf(tipoConsumo.getId()));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String consumoAcumulado(Integer codArduino, TipoConsumo tipoConsumo, Timestamp fechaIni, Timestamp fechaFin){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("consumo")
                .appendPath(String.valueOf(codArduino))
                .appendPath("acumulado")
                .appendQueryParameter("tipo", String.valueOf(tipoConsumo.getId()))
                // por alguna razon se agregan 3 ceros de mas, por eso divido por 1000
                .appendQueryParameter("inicio", String.valueOf(fechaIni.getTime() / 1000))
                .appendQueryParameter("final", String.valueOf(fechaFin.getTime() / 1000));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", "Fecha inicio: " + dateFormat.format(fechaIni.getTime()));
        Log.d("ConstructorUrls", "Fecha fin: " + dateFormat.format(fechaFin.getTime()));
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String login(String usuario, String password){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath("autenticar")
                .appendQueryParameter("username", usuario)
                .appendQueryParameter("pass", password);

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String registro(String usuario, String pass, String mail, PreguntaSeguridad pregSeg, String respPreg){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendQueryParameter("username", usuario)
                .appendQueryParameter("pass", pass)
                .appendQueryParameter("mail", mail)
                .appendQueryParameter("idPregSeg", String.valueOf(pregSeg.getId()))
                .appendQueryParameter("respPreg", respPreg);

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String asociarArduino(Integer idUsuario, Integer idArduino){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath("regArduino")
                .appendQueryParameter("id", String.valueOf(idUsuario))
                .appendQueryParameter("arduino", String.valueOf(idArduino));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String cambiarContraseña(Integer idUsuario, String pass) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(String.valueOf(idUsuario))
                .appendQueryParameter("newPassword", pass);

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String getUsuario(String username){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(username)
                .appendQueryParameter("username", "1");

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String getUsuario(Integer idUsuario){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(String.valueOf(idUsuario));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String estadisticas(Integer codArduino, TipoConsumo tipoConsumo, TipoEstadistica tipoEstadistica, Timestamp fechaHoy){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("consumo")
                .appendPath(String.valueOf(codArduino))
                .appendPath("estadistica")
                .appendQueryParameter("tipo", String.valueOf(tipoConsumo.getId()))
                .appendQueryParameter("periodo", String.valueOf(tipoEstadistica.getId()))
                // por alguna razon se agregan 3 ceros de mas, por eso divido por 1000
                .appendQueryParameter("desde", String.valueOf(fechaHoy.getTime() / 1000));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", "Fecha desde: " + dateFormat.format(fechaHoy.getTime()));
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String guardarLimite(Integer idUsuario, TipoConsumo tipoConsumo, Integer limite){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(String.valueOf(idUsuario))
                .appendPath("setLimite")
                .appendQueryParameter("tipo", String.valueOf(tipoConsumo.getId()))
                .appendQueryParameter("limite", String.valueOf(limite));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }


    public static String factura(Integer idUsuario, TipoConsumo tipoConsumo){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(String.valueOf(idUsuario))
                .appendPath("factura")
                .appendQueryParameter("tipo", String.valueOf(tipoConsumo.getId()));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String tarifa(Double consumo, Integer id_empresa){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("tarifa")
                .appendPath(String.valueOf(consumo))
                .appendQueryParameter("idEmpresa", String.valueOf(id_empresa));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String configAgua(Integer idUsuario, ServicioAgua servAgua, Timestamp fechaUltimaFactura){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(String.valueOf(idUsuario))
                .appendPath("setServ")
                .appendQueryParameter("tipo", String.valueOf(TipoConsumo.AGUA.getId()))
                .appendQueryParameter("k", String.valueOf(servAgua.getK()))
                .appendQueryParameter("zf", String.valueOf(servAgua.getZf()))
                .appendQueryParameter("tgdf", String.valueOf(servAgua.getTgdf()))
                .appendQueryParameter("sc", String.valueOf(servAgua.getSc()))
                .appendQueryParameter("ef", String.valueOf(servAgua.getEf()))
                .appendQueryParameter("st", String.valueOf(servAgua.getSt()))
                .appendQueryParameter("aud", String.valueOf(servAgua.getAud()))
                .appendQueryParameter("fs", String.valueOf((int) servAgua.getFs()))
                .appendQueryParameter("cl", String.valueOf((int) servAgua.getCl()))
                // por alguna razon se agregan 3 ceros de mas, por eso divido por 1000
                .appendQueryParameter("ultimaFactura", String.valueOf(fechaUltimaFactura.getTime() / 1000));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", "Fecha ult factura: " + dateFormat.format(fechaUltimaFactura.getTime()));
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String configElec(int idUsuario, int idEmpresaSelecc, Timestamp fechaUltimaFactura) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath("usuario")
                .appendPath(String.valueOf(idUsuario))
                .appendPath("setServ")
                .appendQueryParameter("tipo", String.valueOf(TipoConsumo.ELECTRICIDAD.getId()))
                .appendQueryParameter("empresa", String.valueOf(idEmpresaSelecc))
                // por alguna razon se agregan 3 ceros de mas, por eso divido por 1000
                .appendQueryParameter("ultimaFactura", String.valueOf(fechaUltimaFactura.getTime() / 1000));

        String url = builder.build().toString();
        Log.d("ConstructorUrls", "Fecha ult factura: " + dateFormat.format(fechaUltimaFactura.getTime()));
        Log.d("ConstructorUrls", url);

        return url;
    }
}
