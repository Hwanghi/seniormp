package org.techtown.seniormp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StepCount.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StepCountDao stepCountDao();
}
