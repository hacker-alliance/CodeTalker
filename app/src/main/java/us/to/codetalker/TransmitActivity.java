package us.to.codetalker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.hardware.camera2.CameraManager;

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

                CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
                try {
                    for (String cameraId : cameraManager.getCameraIdList()) {
                        System.out.print(cameraId);

                    }
                }
                catch (android.hardware.camera2.CameraAccessException e){
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
