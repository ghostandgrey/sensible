package intelliware.ca.sensible.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class GenericListener implements SensorEventListener {

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        System.out.println(String.format("Event: %s", sensorEvent));
        for (int i = 0; i < sensorEvent.values.length; i++) {
            System.out.println(String.format("%s Value[%s] -> %s", sensorEvent.timestamp, i, sensorEvent.values[i]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        System.out.println(String.format("Accuracy changed: %s -> %s", sensor, i));
    }
}
