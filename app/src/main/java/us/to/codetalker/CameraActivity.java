package us.to.codetalker;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import static android.Manifest.permission.CAMERA;

public class CameraActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private static final String    TAG = "OCVSample::Activity";

    private CameraBridgeViewBase   mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_CROSSFADE;
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.rotationAnimation = rotationAnimation;
        win.setAttributes(winParams);

        setContentView(R.layout.activity_camera);

        ActivityCompat.requestPermissions(CameraActivity.this,
                new String[] {CAMERA}, 1);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // run stuff
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        // initialize materials
    }

    public void onCameraViewStopped() {
        // release materials
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat Rgba = inputFrame.rgba();

        int center_x = 2*Rgba.cols()/7;
        int center_y = Rgba.rows()/2;

        int box_size = 100;

        Point s = new Point(center_x-box_size/2,center_y-box_size/2);
        Point f = new Point(center_x+box_size/2,center_y+box_size/2);

        Mat target_area = extractMaterial(Rgba, s, f);
        Imgproc.rectangle(Rgba, s, f, new Scalar(57, 255, 20));

        return Rgba;
    }

    private Mat extractMaterial(Mat input, Point s, Point f) {
        int rows = (int) Math.abs(s.x-f.x);
        int cols = (int) Math.abs(s.y-f.y);

        Mat output = new Mat(rows, cols, 24);

        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                output.put(i, j, input.get(i+((int) s.y), j+((int) s.x)));
            }
        }

        return output;
    }
}
