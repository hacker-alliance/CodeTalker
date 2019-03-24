package us.to.codetalker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.hardware.camera2.*;
import android.util.Log;
import android.os.Handler;
import java.util.ArrayList;
import android.widget.Button;
import android.widget.EditText;


public class TransmitActivity extends AppCompatActivity {

    String message;

    EditText messageInput;

    Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messageInput = findViewById(R.id.messageInput);
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = messageInput.getText().toString();

                CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
                String flashCameraId = "0";

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

                Transmit(PulseProtocol.convertTextToPulses(message),cameraManager,flashCameraId);
             //   Transmit(PulseProtocol.convertTextToPulses(""),cameraManager,flashCameraId);

            }
        });

    }

    public void Transmit(final ArrayList<Boolean> message, final CameraManager cameraManager, final String displayCameraId ){
        for(int i = 0; i < message.size() ; i++) {
            Log.i("HARDCODE"," " + message.get(i));

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
                }, 100*i); // Millisecond 100
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
                }, 100*i); // Millisecond 100
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    cameraManager.setTorchMode(displayCameraId, false);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            }
        }, 100*message.size()); // Millisecond 100

        Log.i("HARDCODE"," " + message.size());
    }
}

