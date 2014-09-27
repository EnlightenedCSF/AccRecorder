package ru.vsu.csf.enlightened.accrecorder;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private EditText editActionName;
    private TextView textStatus;


    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Recieved!");

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt("result");
                if (resultCode == RESULT_OK)
                    textStatus.setText("Stopped.");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editActionName = (EditText) findViewById(R.id.editActionName);
        textStatus = (TextView) findViewById(R.id.textStatus);
    }


    //region Stuff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
    //endregion


    //region Buttons
    public void startRecording(View view) {
        textStatus.setText("Recording...");

        String actionName = editActionName.getText().toString().toLowerCase();

        Intent intent = new Intent(this, AccelerometerService.class);
        intent.putExtra("action", actionName);
        startService(intent);
    }

    public void stopRecording(View view) {
        stopService(new Intent(this, AccelerometerService.class));
    }

    public void clearDB(View view) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DBHelper.TABLE_DATA_NAME + " ;");

        Log.d(TAG, "Table dropped from btn");

        db.close();
    }
    //endregion


}
