package g507.controldeconsumo.conexion;

import org.json.JSONObject;

/**
 * Interface a implementar por fragments que se conecten con la api
 */
public interface TaskListener {
    /**
     * Define que hace cuando se inicia la task
     */
    void inicioTask();

    /**
     * Procesamiento del json obtenido en la consulta
     * @param json
     */
    void finTaskGetUrl(JSONObject json);

    /**
     * Define que hace cuando termina el guardado de datos
     * @param postOk true o false segun si hubo errores
     */
    void finTaskPost(boolean postOk);
}
