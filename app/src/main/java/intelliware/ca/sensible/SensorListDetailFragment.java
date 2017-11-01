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

import com.sccomponents.gauges.ScArcGauge;

import java.util.ArrayList;
import java.util.List;

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
    private ScArcGauge gauge;
    private List<TextView> values = new ArrayList<>();
    private TextView value0;
    private TextView value1;
    private TextView value2;
    private TextView value3;
    private TextView value4;

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
                gauge = rootView.findViewById(R.id.gauge);
            } else {
                rootView = inflater.inflate(R.layout.dummy_sensor, container, false);
                values.add(rootView.findViewById(R.id.value0_value));
                values.add(rootView.findViewById(R.id.value1_value));
                values.add(rootView.findViewById(R.id.value2_value));
                values.add(rootView.findViewById(R.id.value3_value));
                values.add(rootView.findViewById(R.id.value4_value));
            }
        } else {
            rootView = inflater.inflate(R.layout.dummy_sensor, container, false);
        }

        return rootView;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] newValues = sensorEvent.values;
        if (temperatureView != null) {
            temperatureView.setText("" + newValues[0]);
            gauge.setX(newValues[0]);
        } else {
            for (int i = 0; i < newValues.length; i++) {
                if (i < this.values.size()) {
                    this.values.get(i).setText("" + newValues[i]);
                } else {
                    System.out.println(String.format("Value index exceeds max: %s", i));
                }
            }
        }
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
