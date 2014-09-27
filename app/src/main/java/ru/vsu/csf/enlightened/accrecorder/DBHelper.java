package ru.vsu.csf.enlightened.accrecorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by enlightenedcsf on 20.09.14.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DB";
    public static final String TABLE_DATA_NAME = "data";


    public DBHelper(Context context) {
        super(context, "testDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DATA_NAME + "(" +
                        "id_data INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "record_id INTEGER NOT NULL, " +
                        "rot1 REAL NOT NULL, " +
                        "rot2 REAL NOT NULL, " +
                        "rot3 REAL NOT NULL, " +
                        "acc1 REAL NOT NULL, " +
                        "acc2 REAL NOT NULL, " +
                        "acc3 REAL NOT NULL " +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_NAME);

        Log.i(TAG, "Table was dropped");

        onCreate(db);
    }
}
