package g507.controldeconsumo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import g507.controldeconsumo.conexion.ConstructorUrls;
import g507.controldeconsumo.conexion.TaskListener;
import g507.controldeconsumo.conexion.TaskRequestUrl;

public class AsociarFragment extends Fragment implements TaskListener{

    //Tutorial lectura codigo QR
    //http://code.tutsplus.com/tutorials/reading-qr-codes-using-the-mobile-vision-api--cms-24680

    private View view;
    private Button btnAsociar;
    private SurfaceView cameraView;
    private TextView txtResultado;

    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private int codigo = -1;

    private ProgressDialog progressDialog;
    private boolean guardandoDatos = false;

    public AsociarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Detector QR a usar por la cam
        barcodeDetector = new BarcodeDetector.Builder(getActivity())
                .setBarcodeFormats(Barcode.QR_CODE).build();

        //Para leer un stream de imagenes de la camara
        cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_asociar, container, false);
        cameraView = (SurfaceView) view.findViewById(R.id.camera_view);
        txtResultado = (TextView) view.findViewById(R.id.txtResultado);
        btnAsociar = (Button) view.findViewById(R.id.btnAsociarArduino);

        btnAsociar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asociarArduino();
            }
        });

        //Para mostrar las imagenes de la cam al crearse el cuadro y cerrar la cam al destruirse
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException |SecurityException e) {
                    //TODO validar que se tenga el permiso para la cam y sino mostrar msj de error
                    Log.e("CAMERA SOURCE", e.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        //Que hacer al detectar un codigo QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    //Se pone dentro de un post porque el receiveDetections no corre en el hilo UI
                    txtResultado.post(new Runnable() {
                        public void run() {
                            txtResultado.setText(barcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

        return view;
    }

    private void asociarArduino(){
        if(guardandoDatos)
            return;

        String resultado = txtResultado.getText().toString();

        // Valida que se haya leido algo
        if(resultado.equals("")){
            Toast.makeText(getActivity(), R.string.error_qr_arduino, Toast.LENGTH_SHORT).show();
            return;
        }

        // Valida que sea un numero
        try{
            codigo = Integer.parseInt(resultado);
        } catch (NumberFormatException e){
            Toast.makeText(getActivity(), R.string.error_qr_arduino, Toast.LENGTH_SHORT).show();
            return;
        }

        // Leo el id de usuario logeado para guardarle el arduino
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int idUsuario = prefs.getInt(getString(R.string.pref_sesion_inic), -1);

        new TaskRequestUrl(this).execute(ConstructorUrls.asociarArduino(idUsuario, codigo), "POST");
    }

    @Override
    public void inicioRequest() {
        guardandoDatos = true;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere), getString(R.string.msj_cargando), true);
    }

    @Override
    public void finRequest(JSONObject json) {
        guardandoDatos = false;

        if(progressDialog != null)
            progressDialog.dismiss();

        if(json != null){
            try {
                if(json.getString("status").equals("ok")){
                    // Guardo el id del Arduino en SharedPreferences
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    prefs.edit().putInt(getString(R.string.pref_id_arduino), codigo).apply();

                    Toast.makeText(getActivity(), "Arduino asociado correctamente" , Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), getString(R.string.error_traducc_datos) , Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_inesperado_serv) , Toast.LENGTH_SHORT).show();
        }
    }
}
