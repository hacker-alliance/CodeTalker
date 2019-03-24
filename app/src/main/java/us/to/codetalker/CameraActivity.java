package us.to.codetalker;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
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
    private static final int       delayInterval = 100;

    private CameraBridgeViewBase   mOpenCvCameraView;

    private static int             brightnessAverage = 0;
    private static int             brightnessSum = 0;
    private static int             brightnessCount = 0;
    private static int             previous = 0;
    private static int             bitcounter = 0;
    private static float           bitHistory[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static boolean         dataCollected = false;
    private static boolean         preamble = false;
    private static float           lastFrameTime = 0;
    private static float           lastReceiveTime = 0;
    private static float           lastCheckedTime = 0;

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

        mOpenCvCameraView = findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableFpsMeter();

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

        Mat target_area_gray = new Mat(target_area.rows(), target_area.cols(), 24);
        Imgproc.cvtColor(target_area, target_area_gray, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.GaussianBlur(target_area_gray, target_area_gray, new Size(5, 5), 0);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(target_area_gray);
        Mat mask = new Mat(target_area.size(), CvType.CV_8UC1, Scalar.all(0));
        Imgproc.circle(mask, mmr.maxLoc, 5, new Scalar(255, 255, 255), -1);
        double average[] = Core.mean(target_area, mask).val;
        //Log.i(TAG, "MEAN CODETALKER: " + average);
        Imgproc.circle(target_area, mmr.maxLoc, 5, new Scalar(0, 0, 0));
        Rgba = overwriteMaterial(Rgba, target_area);
        //Log.i(TAG, "MEAN CODETALKER2: " + Core.mean(target_area));

        float currentFrameTime = System.currentTimeMillis();
        float deltaTime = currentFrameTime-lastFrameTime;
        lastFrameTime = currentFrameTime;

        if (brightnessCount < 64) {
            brightnessSum += average[0]+average[1]+average[2];

            brightnessCount++;
        } else {
            brightnessAverage = (brightnessSum/64)/3;

            if (!dataCollected) {
                dataCollected = true;
                lastCheckedTime = currentFrameTime;
            }

            brightnessSum = 0;
            brightnessCount = 0;
        }

        if (dataCollected) {
            if (preamble) {
                lastReceiveTime += deltaTime;

                if ((average[0] + average[1] + average[2]) / 3 < brightnessAverage) {
                    if (previous == 1) {
                        System.out.println("READING: 0");
                        previous = 0;
                        lastReceiveTime = lastReceiveTime - delayInterval;
                    } else if (lastReceiveTime > delayInterval) {
                        System.out.println("READING: 0");
                        lastReceiveTime = lastReceiveTime - delayInterval;
                    }
                } else {
                    if (previous == 0) {
                        System.out.println("READING: 1");
                        previous = 1;
                        lastReceiveTime = lastReceiveTime - delayInterval;
                    } else if (lastReceiveTime > delayInterval) {
                        System.out.println("READING: 1");
                        lastReceiveTime = lastReceiveTime - delayInterval;
                    }
                }

                //System.out.println("READING: " + lastReceiveTime);
            } else {
                if ((average[0] + average[1] + average[2]) / 3 < brightnessAverage) {
                    if (previous == 1) {
                        if (bitcounter < 16) {
                            bitcounter++;
                        } else if (bitHistory[15] != 0) {
                            for (int i = 1; i<16; i++) {
                                bitHistory[i-1] = bitHistory[i];
                            }
                        }

                        //System.out.println("READING: 0");
                        previous = 0;
                        bitHistory[bitcounter-1] = currentFrameTime-lastCheckedTime;// delay interval
                        lastCheckedTime = currentFrameTime;
                    }
                } else {
                    if (previous == 0) {
                        if (bitcounter < 16) {
                            bitcounter++;
                        } else if (bitHistory[15] != 0) {
                            for (int i = 1; i<16; i++) {
                                bitHistory[i-1] = bitHistory[i];
                            }
                        }

                        //System.out.println("READING: 1");
                        previous = 1;
                        bitHistory[bitcounter-1] = currentFrameTime-lastCheckedTime;// delay interval
                        lastCheckedTime = currentFrameTime;
                    }
                }

                if (bitcounter == 16) {
                    float sum = 0;
                    for (float delay : bitHistory) {
                        sum += delay;
                    }

                    if (sum < 1600) {
                        lastReceiveTime = currentFrameTime-lastCheckedTime;
                        preamble = true;
                    }
                }
            }
        }

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

    private Mat overwriteMaterial(Mat source, Mat input) {
        int rows = input.cols();
        int cols = input.rows();

        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                source.put(i, j, input.get(i, j));
            }
        }

        return source;
    }
}
