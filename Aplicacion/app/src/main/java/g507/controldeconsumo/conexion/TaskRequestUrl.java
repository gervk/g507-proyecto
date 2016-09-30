package g507.controldeconsumo.conexion;

import android.os.AsyncTask;

import org.json.JSONObject;

public class TaskRequestUrl extends AsyncTask<String, Void, JSONObject> {

    private TaskListener listener;

    public TaskRequestUrl(TaskListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.inicioRequest();
    }

    //Recibe un array de string de parametros del execute():
    // el 1 es la url y el 2 el metodo (GET/POST)
    @Override
    protected JSONObject doInBackground(String... params) {
        String url = params[0];
        String metodo = params[1];

        String respuesta = Utils.requestUrl(url, metodo);
        if(respuesta != null){
            return Utils.parsearAJson(respuesta);
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        listener.finRequest(jsonObject);
    }
}
