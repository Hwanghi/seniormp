package org.techtown.seniormp;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

public class CustomScannerActivity extends Activity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchCameraBtn;
    private int switchCameraState;
    CameraSettings cameraSettings = new CameraSettings();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

    //    switchCameraState = Camera.CameraInfo.CAMERA_FACING_BACK;

        switchCameraBtn = (ImageButton) findViewById(R.id.btn_switchCamera);

        if (Camera.getNumberOfCameras() < 2) {
            switchCameraBtn.setVisibility(View.GONE);
        }

        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    public void switchCamera(View view) {

        Toast.makeText(this, "카메라 전환",Toast.LENGTH_SHORT).show();
        if (switchCameraState == Camera.CameraInfo.CAMERA_FACING_FRONT) {

            cameraSettings.setRequestedCameraId(0);
            barcodeScannerView.getBarcodeView().setCameraSettings(cameraSettings);
            switchCameraState = 0;
            barcodeScannerView.pause();
            barcodeScannerView.resume();

        } else {
            //Toast.makeText(this, cameraSettings.getRequestedCameraId(),Toast.LENGTH_LONG).show();

            cameraSettings.setRequestedCameraId(1);
            barcodeScannerView.getBarcodeView().setCameraSettings(cameraSettings);
            switchCameraState = 1;
            barcodeScannerView.pause();
            barcodeScannerView.resume();
        }
     //   Toast.makeText(this, switchCameraState,Toast.LENGTH_LONG).show();

    }
}
