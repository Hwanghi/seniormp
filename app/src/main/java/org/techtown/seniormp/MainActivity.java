package org.techtown.seniormp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.seniormp.com/"));
        startActivity(intent);
    }

    public void onButton2Clicked(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UC0QKydIcUMagoPnBxFqGjvg"));
        startActivity(intent);
    }


    public void onButton3Clicked(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.seoulmetro.co.kr/kr/cyberStation.do"));
        startActivity(intent);
    }

    public void onButton4Clicked(View view) {
        scanCode();
    }

    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(CustomScannerActivity.class);
//        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        // integrator.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT); //
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scanning Result");
                builder.setPositiveButton("다시 스캔하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
                    }
                }).setNegativeButton("링크 열기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
            else {
                //       Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();

            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onButton5Clicked(View view){
        Intent intent = new Intent(this, StepCounter.class);
        startActivity(intent);



    }

}
