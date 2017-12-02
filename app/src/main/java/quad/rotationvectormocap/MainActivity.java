package quad.rotationvectormocap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener, ActivityCompat.OnRequestPermissionsResultCallback {

    SensorManager sensorManager;
    Sensor rotationVectorSensor;
    boolean recording = false;
    String recordedData = "";
    private View layout;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    Context activity;
    int fileNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        //Get an instance of the sensormanager
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.main_layout);
        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
            Log.v("RAN", "YEP");
        }*/
        //Just adds a button to be able to decide when to record and not.
        final Button recordButton = findViewById(R.id.recordbutton);
        recordButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                recording = !recording;
                if (recordButton.getText().equals("Record"))
                {
                    recordButton.setText("Stop Recording");
                }
                else if(recordButton.getText().equals("Stop Recording"))
                {
                    recordButton.setText("Record");
                }

            }
        });
        final Button saveButton = findViewById(R.id.savetofilebutton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!recording) {
                    //I find the really verboseness of android studio to be really stupid.
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            //myFile.createNewFile("RecordedData.txt");

                            File myFile = new File("sdcard/DCIM/recordedMoCapData"+ fileNumber + ".txt");
                            //path.mkdirs();
                            FileOutputStream fOut = new FileOutputStream(myFile);
                            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                            myOutWriter.append(recordedData);
                            myOutWriter.close();
                            fOut.close();
                            Toast.makeText(v.getContext(), "Done writing SD 'recordedMoCapData" + fileNumber + ".txt'", Toast.LENGTH_SHORT).show();
                            fileNumber++;
                            recordedData = "";
                        } catch (Exception e) {
                            Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        requestStoragePermission();
                    }

                }
                else
                {
                    Toast.makeText(v.getContext(), "You have to stop recording to save to file.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            //Calendar calendar = Calendar.;
            //Log.v("SDCARD", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
            String string = String.format("%.4f,%.4f,%.4f,%.4f" + " timestamp: " + Calendar.getInstance().getTimeInMillis() + "\n", event.values[0], event.values[1], event.values[2], event.values[3], event.values[4]);
            ((TextView)findViewById(R.id.xRotation)).setText("X: " + event.values[0]);
            ((TextView)findViewById(R.id.yRotation)).setText("Y: " + event.values[1]);
            ((TextView)findViewById(R.id.zRotation)).setText("Z: " + event.values[2]);
            ((TextView)findViewById(R.id.wRotation)).setText("W: " + event.values[3]);

            if (recording)
            {
                recordedData = recordedData + string;
            }
        }
    }

    /**
     * Requests the {@link android.Manifest.permission_group#STORAGE} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     *
     * Also, this is so fucking unnecessary, why not just make it into a method with a standard format.
     */
    private void requestStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(layout, "Need access to write to the SD card.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Snackbar.make(layout,
                    "Permission is not available. Requesting write permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }

        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(layout, "Need access to read the SD Card.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Snackbar.make(layout,
                    "Permission is not available. Requesting read permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

}
