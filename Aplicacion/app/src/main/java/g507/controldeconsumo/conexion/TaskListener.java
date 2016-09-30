package g507.controldeconsumo.conexion;

import org.json.JSONObject;

/**
 * Interface a implementar por fragments que se conecten con la api
 */
public interface TaskListener {
    /**
     * Define que hace cuando se inicia la request
     */
    void inicioRequest();

    /**
     * Procesamiento del json obtenido en el request
     * @param json
     */
    void finRequest(JSONObject json);

}
