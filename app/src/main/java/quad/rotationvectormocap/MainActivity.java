package quad.rotationvectormocap;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor rotationVectorSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get an instance of the sensormanager
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, rotationVectorSensor, 10000);
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
            String string = String.format("%.2f \t %.2f \t %.2f \t %.2f \t %.2f", event.values[0], event.values[1], event.values[2], event.values[3], event.values[4]);
            Log.v("EventValues", string);
            //Log.v("EventValues", event.values[0] + "\t\t" + event.values[1] + "\t\t" + event.values[2] + "      Len: " + event.values.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

}
