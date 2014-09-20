package ru.vsu.csf.enlightened.accrecorder;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by enlightenedcsf on 20.09.14.
 */
public class AccelerometerService extends IntentService implements SensorEventListener {


    private class AccelerometerData implements Serializable{

        private SensorManager msensorManager; //Менеджер сенсоров аппрата

        private float[] rotationMatrix;     //Матрица поворота
        private float[] accelData;           //Данные с акселерометра
        private float[] magnetData;       //Данные геомагнитного датчика
        private float[] orientationData; //Матрица положения в пространстве

        private LinkedList<Float[]> history;

        public AccelerometerData() {
            //msensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

            rotationMatrix = new float[16];
            accelData = new float[3];
            magnetData = new float[3];
            orientationData = new float[3];
            history = new LinkedList<Float[]>();
        }

        public LinkedList<Float[]> getHistory() {
            return history;
        }

        public void setHistory(LinkedList<Float[]> history) {
            this.history = history;
        }

        public SensorManager getMsensorManager() {
            return msensorManager;
        }

        public void setMsensorManager(SensorManager msensorManager) {
            this.msensorManager = msensorManager;
        }

        public float[] getRotationMatrix() {
            return rotationMatrix;
        }

        public void setRotationMatrix(float[] rotationMatrix) {
            this.rotationMatrix = rotationMatrix;
        }

        public float[] getAccelData() {
            return accelData;
        }

        public void setAccelData(float[] accelData) {
            this.accelData = accelData;
        }

        public float[] getMagnetData() {
            return magnetData;
        }

        public void setMagnetData(float[] magnetData) {
            this.magnetData = magnetData;
        }

        public float[] getOrientationData() {
            return orientationData;
        }

        public void setOrientationData(float[] orientationData) {
            this.orientationData = orientationData;
        }
    }


    //region Declarations
    public static final String TAG = "Service";

    private int result = Activity.RESULT_CANCELED;
    private AccelerometerData data  = new AccelerometerData();

    private Timer timer;
    private TimerTask task;

    private DBHelper myDBHelper;
    private String actionName;
    //endregion


    public AccelerometerService() {
        super("AccelerometerService");

        myDBHelper = new DBHelper(this);
        timer = new Timer();

        task = new TimerTask() {

            @Override
            public void run() {
                Log.i(TAG, "tick");
                /*float[] arr = AccelerometerService.this.data.getAccelData();
                Float[] newArr = new Float[3];
                for (int i = 0; i < arr.length; i++) {
                    newArr[i] = arr[i];
                }
                AccelerometerService.this.data.history.add(newArr);*/
            }
        };

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Service started");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            actionName = bundle.getString("action");
        }
        else
            actionName = "noName";

        data.history.clear();

        int delay = 1000;
        int period = 500;

        timer.scheduleAtFixedRate(task, delay, period);
    }

    @Override
    public boolean stopService(Intent name) {
        timer.cancel();
        task.cancel();

        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Log.i(TAG, "Service stopped.");

        result = Activity.RESULT_OK;
        publishResults();
        return super.stopService(name);
    }


    private void publishResults() {
        Intent intent  = new Intent(TAG);
        intent.putExtra("result", result);
        intent.putExtra("data", data);
        sendBroadcast(intent);
    }


    //region Sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        //this.loadNewSensorData(event);
        //SensorManager.getRotationMatrix(data.getRotationMatrix(), null, data.getAccelData(), data.getMagnetData());
        //SensorManager.getOrientation(data.getRotationMatrix(), data.getOrientationData());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //...
    }

    private void loadNewSensorData(SensorEvent event) {
        final int type = event.sensor.getType();    //Определяем тип датчика
        if (type == Sensor.TYPE_ACCELEROMETER) {    //Если акселерометр
            data.setAccelData(event.values.clone());
        }

        if (type == Sensor.TYPE_MAGNETIC_FIELD) {   //Если геомагнитный датчик
            data.setMagnetData(event.values.clone());
        }
    }
    //endregion
}
