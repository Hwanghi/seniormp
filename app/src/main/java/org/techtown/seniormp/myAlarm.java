package org.techtown.seniormp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.room.Room;

import java.util.List;

public class myAlarm extends BroadcastReceiver {
    private static final String TAG = "MY_ALARM";

    int step;
    int stepRecord;  // 전달 받은 걸음수.
    StepCount stepCountBefore, stepCount2db;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: just received the Alarm");
       // if (intent.getAction() == "android.intent.action.BOOT_COMPLETED")
        stepRecord = intent.getIntExtra("stepRecord", -1); // 인텐트로 현재 stepRecord 전달 받음.
        Log.d(TAG, "onReceive: intent.getIntExtra(\"stepRecord\",-1);\n stepRecord=>" + stepRecord);

        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "StepCount")
                .allowMainThreadQueries()
                .build();
        List<StepCount> stepCountList = (List<StepCount>) db.stepCountDao().getAll(); // db읽기.
        Log.d(TAG, "onReceive: stepCountList.size() => " + stepCountList.size());
        if(stepCountList.size() > 0 ) {
            stepCountBefore = stepCountList.get(stepCountList.size() - 1); // 리스트의 마지막 stepCount 객체.
            step = stepRecord - stepCountBefore.getStepRecord(); // 오늘 = 현재 - 과거. 삽입할 객체 생성.
            stepCount2db = new StepCount(stepRecord);
            stepCountList.add(stepCount2db);
            db.stepCountDao().insert(stepCount2db); // db에 수정.
            Log.d(TAG, "onReceive: db.stepCountDao().insert(stepCount2db); (1) => " + stepCount2db);
        }
        else if(stepCountList.size() == 0) {
            step = stepRecord;
            stepCount2db = new StepCount(stepRecord);

            db.stepCountDao().insert(stepCount2db);   // db에 삽입.
            Log.d(TAG, "onReceive: db.stepCountDao().insert(stepCount2db); (2) => " + stepCount2db);

        }
    }
}
