package us.to.codetalker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.hardware.camera2.*;
import android.util.Log;
import android.os.Handler;
import java.util.ArrayList;

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

             final  CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
             final  String flashCameraId;

                try {
                    for (String cameraId : cameraManager.getCameraIdList()) {
                        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                        Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                        if (hasFlash != null && hasFlash) { // If this camera ID has flash
                            flashCameraId = cameraId;
                            Log.i("HARDCODE",flashCameraId + " Has Flash");
                            break;
                        }

                    }
                }
                catch (CameraAccessException e){
                    e.printStackTrace();
                }

            }


        });
    }

    public void toggleFlash(CameraManager cameraManager, String flashCameraId, boolean status){
        try {
            cameraManager.setTorchMode(flashCameraId, status);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void Transmit(final ArrayList<Boolean> message, final CameraManager cameraManager, final String displayCameraId ){

        for(int i = 0; i < message.size() ; i++) {
            if (message.get(i)) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            cameraManager.setTorchMode(displayCameraId, true);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }

                    }
                }, 100); // Millisecond 100
            }
            else{
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            cameraManager.setTorchMode(displayCameraId, false);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }

                    }
                }, 100); // Millisecond 100
            }
        }
    }

}

