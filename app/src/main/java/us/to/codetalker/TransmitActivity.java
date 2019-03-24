package us.to.codetalker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.hardware.camera2.*;
import android.util.Log;

public class TransmitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flashCameraId;

                CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
                try {
                    for (String cameraId : cameraManager.getCameraIdList()) {
                        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                        Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                        if (hasFlash != null && hasFlash) { // If this camera ID has flash
                            flashCameraId = cameraId;
                            Log.i("HARDCODE",flashCameraId + " Has Flash");
                        }


                    }
                }
                catch (CameraAccessException e){
                    e.printStackTrace();
                }



          //      Camera cam = Camera.open();
          //      Parameters p = cam.getParameters();
          //      p.setFlashMode(Parameters.FLASH_MODE_TORCH);
          //      cam.setParameters(p);
          //      cam.startPreview();
            }
        });
    }

}
