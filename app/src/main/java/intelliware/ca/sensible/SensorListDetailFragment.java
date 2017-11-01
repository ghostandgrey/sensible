package intelliware.ca.sensible;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A fragment representing a single SensorList detail screen.
 * This fragment is either contained in a {@link SensorListActivity}
 * in two-pane mode (on tablets) or a {@link SensorListDetailActivity}
 * on handsets.
 */
public class SensorListDetailFragment extends Fragment implements SensorEventListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private Sensor sensor = null;
    private SensorManager sensorManager = null;
    private TextView temperatureView = null;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SensorListDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
            sensor = SensorListActivity.SENSOR_MAP.get(getArguments().getString(ARG_ITEM_ID));

            if (sensorManager != null) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(sensor.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        // Show the dummy content as text in a TextView.
        if (sensor != null) {
            if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                rootView = inflater.inflate(R.layout.temperature_sensor, container, false);
                temperatureView = rootView.findViewById(R.id.temperature);
                temperatureView.setText("" + sensor.getPower());
            } else {
                rootView = inflater.inflate(R.layout.dummy_sensor, container, false);
                ((TextView) rootView.findViewById(R.id.dummy_type)).setText(sensor.getStringType());
                ((TextView) rootView.findViewById(R.id.dummy_vendor)).setText(sensor.getVendor());
            }
        } else {
            rootView = inflater.inflate(R.layout.dummy_sensor, container, false);
        }

        return rootView;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        temperatureView.setText("" + sensorEvent.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        System.out.println(String.format("Accuracy changed: %s -> %s", sensor, i));
    }

    @Override
    public void onPause() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}
