package org.techtown.seniormp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class StepCount {

    @PrimaryKey @NonNull
    public String dateString;

    private int stepRecord;

    public StepCount(int stepRecord) {
//        time = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm"); // 연월일만 나오게 한 후 인덱스로 사용.
        dateString = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        this.stepRecord = stepRecord;
    }

    public int getStepRecord() { return stepRecord; }

    public void setStepRecord(int stepRecord) {
        this.stepRecord = stepRecord;
    }

    @Override
    public String toString() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd"); // 연월일만 나오게 한 후 인덱스로 사용.
//        Date date = new Date(time);
//        String dateString = simpleDateFormat.format(date);
        final StringBuffer sb = new StringBuffer("StepCount{");
        sb.append("date=").append(dateString);
        sb.append(", stepRecord=").append(stepRecord);
        sb.append("}\n");

        return sb.toString();
    }
}
