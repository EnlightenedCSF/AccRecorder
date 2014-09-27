package ru.vsu.csf.enlightened.accrecorder;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by enlightenedcsf on 20.09.14.
 */
public class AccelerometerService extends Service implements SensorEventListener {

    private final String TAG = "AccService";

    private static int recordId = 1;

    private SensorManager sensorManager;   //Менеджер сенсоров аппрата

    private float[] rotationMatrix;         //Матрица поворота
    private float[] accelData;              //Данные с акселерометра
    private float[] magnetData;             //Данные геомагнитного датчика
    private float[] orientationData;        //Матрица положения в пространстве

    private Thread taskThread;

    private ArrayList<float[]> accelHistory;
    private ArrayList<float[]> rotHistory;

    private DBHelper dbHelper;

    public AccelerometerService() {
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        accelHistory = new ArrayList<float[]>();
        rotHistory = new ArrayList<float[]>();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        rotationMatrix = new float[16];
        accelData = new float[3];
        magnetData = new float[3];
        orientationData = new float[3];

        dbHelper = new DBHelper(AccelerometerService.this);

        taskThread = new Thread(new Runnable() {
            @Override
            public void run() {

                sensorManager.registerListener(AccelerometerService.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
                sensorManager.registerListener(AccelerometerService.this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);

                boolean finished = false;

                while (!finished) {

                    accelHistory.add(accelData);
                    rotHistory.add(orientationData);

                    if (accelHistory.size() == 70) {

                        Log.d(TAG, "writing!");

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();

                        for (int i = 0; i < accelHistory.size(); i++) {
                            cv.put("record_id", recordId);

                            cv.put("rot1", rotHistory.get(i)[0]);
                            cv.put("rot2", rotHistory.get(i)[1]);
                            cv.put("rot3", rotHistory.get(i)[2]);

                            cv.put("acc1", accelHistory.get(i)[0]);
                            cv.put("acc2", accelHistory.get(i)[1]);
                            cv.put("acc3", accelHistory.get(i)[2]);

                            db.insert(DBHelper.TABLE_DATA_NAME, null, cv);
                        }

                        recordId++;

                        Log.d(TAG, recordId+"");

                        accelHistory.clear();
                        rotHistory.clear();
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                    catch (InterruptedException e) {
                        finished = true;
                    }
                }

            }
        });
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        record(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        taskThread.interrupt();
        sensorManager.unregisterListener(AccelerometerService.this);
        Log.d(TAG, "onDestroy");
    }


    void record(Intent intent) {
        taskThread.start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        this.loadNewSensorData(event);
        SensorManager.getRotationMatrix(rotationMatrix, null, accelData, magnetData);
        SensorManager.getOrientation(rotationMatrix, orientationData);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void loadNewSensorData(SensorEvent event) {
        final int type = event.sensor.getType();    //Определяем тип датчика
        if (type == Sensor.TYPE_ACCELEROMETER) {    //Если акселерометр
            accelData = event.values.clone();
        }

        if (type == Sensor.TYPE_MAGNETIC_FIELD) {   //Если геомагнитный датчик
            magnetData = event.values.clone();
        }
    }
}
