package intelliware.ca.sensible;

import android.content.res.Resources;
import android.hardware.SensorEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface SensorAdapter {
    View createView(LayoutInflater inflater, ViewGroup container, Resources resources);

    void onSensorChanged(SensorEvent sensorEvent);
}
