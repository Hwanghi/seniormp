package org.techtown.seniormp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class StepCounter extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "STEP_COUNTER";

    private TextView tvStepCounter, tv_record;
    private FrameLayout graphFrame;
    private View graphView;
    FrameLayout.LayoutParams layoutParams;
    private Button btn_start_stop, btn_reset;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    int stepRecord;
    boolean onOff = false; // 시작

    private AlarmManager alarmManager;
    private final StepCount stepCount = new StepCount(stepRecord);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: onOff=>"+onOff);

        setContentView(R.layout.activity_step_counter);
        tvStepCounter = findViewById(R.id.tv_stepCounter);

        graphFrame = findViewById(R.id.graph_total);
        graphView = findViewById(R.id.graph_today_stepCounter);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepCounterSensor == null) {
            Toast.makeText(this,"걷기 감지 센서가 없습니다.", Toast.LENGTH_LONG).show();
        }

        btn_start_stop = findViewById(R.id.btn_start_stop);
        if(onOff) {
            btn_start_stop.setText("중지");
            btn_start_stop.setBackgroundColor(Color.RED);

        } else {
            btn_start_stop.setText("시작");
            btn_start_stop.setBackgroundColor(Color.GREEN);
        }

        btn_reset = findViewById(R.id.btn_reset);

        View.OnClickListener onClickListener = v -> {
            switch (v.getId()){
                case R.id.btn_start_stop:
                    Log.d(TAG, "onClick: Btn_start_stop: stpeRecord"+stepRecord +"onOff=>"+onOff);
                    if(onOff) {
                        onOff = false; // 시작. 만보기 작동중 아님
                        v.setBackgroundColor(Color.GREEN);
                        ((Button) v).setText("시작");
                        sensorManager.unregisterListener(this);
                    } else {
                        onOff = true;  // 중지. 만보기 작동중
                        v.setBackgroundColor(Color.RED);
                        ((Button) v).setText("중지");
                    }
                    Log.d(TAG, "onClick: Btn_start_stop: stpeRecord"+stepRecord +"onOff=>"+onOff);
                    break;
                case R.id.btn_reset :
                    stepRecord = 0;
                    tvStepCounter.setText(String.valueOf(stepRecord));
                    btn_start_stop.setBackgroundColor(Color.GREEN);
                    btn_start_stop.setText("시작");
                    onOff = false;  // -> 중지
                    Log.d(TAG, "onClick: btn_reset stepRecord=>" + stepRecord);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }
        };
//        try (FileOutputStream fos =  this.openFileOutput("state", MODE_PRIVATE)) {
//                fos.write((byte)(onOff ? 1 : 0));
//            } catch (IOException e) {
//            e.printStackTrace();
//        }


        btn_start_stop.setOnClickListener(onClickListener);
        btn_reset.setOnClickListener(onClickListener);

        tv_record = findViewById(R.id.tv_record);
        tv_record.setMovementMethod(new ScrollingMovementMethod());
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class,"StepCount")
                .allowMainThreadQueries()
                .build();
        // getall() 값이 변경될 때마다 수행
        db.stepCountDao().getAll().observe(this, stepCounts -> {
            tv_record.setText(stepCounts.toString());  // db에서 읽어오기.
            stepRecord = stepCounts.get(stepCounts.size()-1).getStepRecord();
            tvStepCounter.setText(String.valueOf(stepRecord));
            Log.d(TAG, "onCreate: stepCounts.get(stepCounts.size()-1).getStepRecord() => "+stepCounts.get(stepCounts.size()-1).getStepRecord());
        });
    }

//    private void setAlarmManager(){
//        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 15);
//        calendar.set(Calendar.MINUTE, 10);
//
//        Intent intent = new Intent(this, myAlarm.class);
//        intent.putExtra("stepRecord", (int) stepRecord);
//        Log.d(TAG, "setAlarmManager(): intent.putExtra(\"stepRecord\", (int) stepRecord);\n (int) stepRecord =>" + (int) stepRecord);
//        Log.d(TAG, "setAlarmManager(): intent.getIntExtra(\"stepRecord\") => " + intent.getIntExtra(("stepRecord"), -1));
//
//        PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(),operation);
//        // 특정 시각 마다 브로드캐스트하여 누적걸음수 전달.
//
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged");

        float ratioStep = (float) stepRecord / 10000f;
        layoutParams = new FrameLayout.LayoutParams(graphFrame.getWidth(),graphFrame.getHeight());
        layoutParams.width = (int) (ratioStep * (float) graphFrame.getWidth());
        layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        graphView.setLayoutParams(layoutParams);
        Log.d(TAG, "layoutParams.width=>"+layoutParams.width + "\nlayoutParams.height=>"+layoutParams.height);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,stepCounterSensor,SensorManager.SENSOR_DELAY_NORMAL);
        restoreState();
        Log.d(TAG, "onResume에서 stepRecord => "+stepRecord);
//        db.stepCountDao().getAll().getValue().get()
    }

    @Override
    protected void onPause() {
        super.onPause();
        stepCount.setStepRecord(stepRecord);
        AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "StepCount")
                .allowMainThreadQueries()
                .build();
        db.stepCountDao().insert(stepCount);
        saveState();
        Log.d(TAG, "onPause: db.stepCountDao().insert(stepCount);: "+ stepCount);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: stepRecord => " + stepRecord);

        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {  // 걸음이 감지되면
            if (onOff) {
                tvStepCounter.setText(String.valueOf(++stepRecord));  // 기기 내 누적걸음수 표시.
                float stepRatio = stepRecord / 10000f; // 만보 중에 걸은 비율.
                layoutParams.width = (int) (stepRatio * graphFrame.getWidth());
                layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
                graphView.setLayoutParams(layoutParams);

                // setAlarmManager();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    protected void saveState() {
        SharedPreferences sharedPref = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("onOff", onOff);
        editor.apply();
        Log.d(TAG, "saveState: editor=>"+editor);
    }

    protected void restoreState() {
        SharedPreferences sharedPref = this.getPreferences(MODE_PRIVATE);
        onOff = sharedPref.getBoolean("onOff", false);
        if(onOff) {
            btn_start_stop.setText("중지");
            btn_start_stop.setBackgroundColor(Color.RED);

        } else {
            btn_start_stop.setText("시작");
            btn_start_stop.setBackgroundColor(Color.GREEN);
        }
        Log.d(TAG, "restoreState: sharedPref=>"+sharedPref);
    }
}
