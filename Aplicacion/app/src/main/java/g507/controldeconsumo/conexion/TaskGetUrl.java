package g507.controldeconsumo.conexion;

import android.os.AsyncTask;

import org.json.JSONObject;

public class TaskGetUrl extends AsyncTask<String, Void, JSONObject> {

    private TaskListener listener;

    public TaskGetUrl(TaskListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.inicioTask();
    }

    //Recibe parametros del execute()
    @Override
    protected JSONObject doInBackground(String... url) {
        String respuesta = Utils.getUrl(url[0]);
        if(respuesta != null){
            return Utils.parsearAJson(respuesta);
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        listener.finTaskGetUrl(jsonObject);
    }
}
