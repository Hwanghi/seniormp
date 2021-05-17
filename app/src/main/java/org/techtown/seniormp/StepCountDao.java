package org.techtown.seniormp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
interface StepCountDao {    // Data Access Object
    @Query("SELECT * FROM StepCount")
    LiveData<List<StepCount>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StepCount stepCount);

    @Update
    void update(StepCount stepCount);

    @Delete
    void delete(StepCount stepCount);

}
