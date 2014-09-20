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
    public static final String TABLE_NAMES_NAME = "names";
    public static final String TABLE_DATA_NAME = "data";


    public DBHelper(Context context) {
        super(context, "testDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating DB...");

        db.execSQL("CREATE TABLE " + TABLE_NAMES_NAME + "(" +
                        "id_action INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "desc TEXT" + ");"
        );

        db.execSQL("CREATE TABLE " + TABLE_DATA_NAME + "(" +
                        "id_data INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_action INTEGER NOT NULL, " +
                        "data1 REAL NOT NULL, " +
                        "data2 REAL NOT NULL, " +
                        "data3 REAL NOT NULL, " +
                        "FOREIGN KEY (id_action) REFERENCES " + TABLE_NAMES_NAME + "(id_action);"
        );

        Log.i(TAG, "DB created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMES_NAME);

        Log.i(TAG, "Tables are dropped");

        onCreate(db);
    }
}
