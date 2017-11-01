package intelliware.ca.sensible;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
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
import com.sccomponents.gauges.ScCopier;
import com.sccomponents.gauges.ScFeature;
import com.sccomponents.gauges.ScGauge;
import com.sccomponents.gauges.ScNotches;
import com.sccomponents.gauges.ScPointer;
import com.sccomponents.gauges.ScWriter;

import java.text.DecimalFormat;
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
    private List<TextView> sensorValues = new ArrayList<>();
    private TextView value0;
    private TextView value1;
    private TextView value2;
    private TextView value3;
    private TextView value4;
    private View rootView = null;

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
        // Show the dummy content as text in a TextView.
        if (sensor != null) {
            if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                rootView = inflater.inflate(R.layout.temperature_sensor, container, false);
                setUpView();
            } else {
                rootView = inflater.inflate(R.layout.dummy_sensor, container, false);
                sensorValues.add(rootView.findViewById(R.id.value0_value));
                sensorValues.add(rootView.findViewById(R.id.value1_value));
                sensorValues.add(rootView.findViewById(R.id.value2_value));
                sensorValues.add(rootView.findViewById(R.id.value3_value));
                sensorValues.add(rootView.findViewById(R.id.value4_value));
                for (TextView valueView : sensorValues) {
                    valueView.setText("");
                }
            }
        } else {
            rootView = inflater.inflate(R.layout.dummy_sensor, container, false);
        }

        return rootView;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] newValues = sensorEvent.values;
        if (rootView != null && sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {

            ((TextView) rootView.findViewById(R.id.temperature)).setText("" + sensorEvent.values[0]);

            rootView.<TextView>findViewById(R.id.temperature_counter).setText(sensorEvent.values[0] + "°");
        } else {
            for (TextView textView : sensorValues) {
                textView.setText("");
            }
            int i = 0;
            for (; i < sensorValues.size() && i < newValues.length; i++) {
                this.sensorValues.get(i).setText(new DecimalFormat("0.000").format(newValues[i]));
            }
            for (; i < newValues.length; i++) {
                System.out.println(String.format("Value index exceeds max: %s", i));
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

    public void setUpView() {
        // Find the components
        final ScArcGauge gauge = rootView.findViewById(R.id.temperature_gauge);
        assert gauge != null;

        final TextView counter = rootView.findViewById(R.id.temperature_counter);
        assert counter != null;

        // Create a drawable
        final Bitmap indicator = BitmapFactory.decodeResource(this.getResources(), R.drawable.indicator);

        // Set the values.
        gauge.setHighValue(14, -20, 30);
        gauge.setPathTouchThreshold(40);

        // Set colors of the base
        int[] colours = {
                Color.parseColor("#15B7FF"), Color.parseColor("#15B7FF"),
                Color.parseColor("#98CA06"), Color.parseColor("#98CA06"),
                Color.parseColor("#98CA06"), Color.parseColor("#98CA06"),
                Color.parseColor("#98CA06"), Color.parseColor("#DC1E10")
        };
        gauge.setProgressColors(colours);
        gauge.setStrokeColors(colours);
        gauge.setTextColors(colours);
        gauge.setStrokeColorsMode(ScFeature.ColorsMode.SOLID);

        // Writer
        String[] tokens = new String[9];
        for (int index = 0; index < 9; index++) {
            tokens[index] = Integer.toString(index * 5 - 10);
        }
        gauge.setTextTokens(tokens);

        ScWriter writer = (ScWriter) gauge.findFeature(ScGauge.WRITER_IDENTIFIER);
        writer.setLastTokenOnEnd(true);

        gauge.setOnEventListener(new ScGauge.OnEventListener() {
            @Override
            public void onValueChange(float lowValue, float highValue) {
                // Write the value
                highValue = ScGauge.percentageToValue(highValue, -20, 30);
                float round = (Math.round(highValue * 10.0f)) / 10.0f;
                counter.setText(Float.toString(round) + "°");
            }
        });

        gauge.setOnDrawListener(new ScGauge.OnDrawListener() {
            @Override
            public void onBeforeDrawCopy(ScCopier.CopyInfo info) {
                // Check if is the progress
                if (info.source.getTag() == ScGauge.PROGRESS_IDENTIFIER) {
                    // Scale and adjust the offset
                    info.scale = new PointF(1.1f, 1.1f);
                    info.offset = new PointF(-28, -28);

                    // Adjust the color
//                    int color = base.getGradientColor(gauge.getHighValue(), 100);
//                    info.source.getPainter().setColor(color);
                }
            }

            @Override
            public void onBeforeDrawNotch(ScNotches.NotchInfo info) {
                // Move the offset
                info.offset = -info.length / 2;

                // Check for module highlight the notches than have module 5 and 10
                if (info.index % 10 == 0) {
                    info.size = 6;
                } else if (info.index % 5 != 0) {
                    info.length -= 5;
                }
            }

            @Override
            public void onBeforeDrawPointer(ScPointer.PointerInfo info) {
                // Check if the pointer if the high pointer
                if (info.source.getTag() == ScGauge.HIGH_POINTER_IDENTIFIER) {
                    // Adjust the offset
                    info.offset.x = -indicator.getWidth() / 2;
                    info.offset.y = -indicator.getHeight() / 2 - gauge.getStrokeSize();
                    // Assign the bitmap to the pointer info structure
                    info.bitmap = indicator;
                }
            }

            @Override
            public void onBeforeDrawToken(ScWriter.TokenInfo info) {
                // Center on the notches
                Rect bounds = new Rect();
                info.source.getPainter().getTextBounds(info.text, 0, info.text.length(), bounds);
                info.offset.x = -(bounds.width() / 2) - 1;
            }
        });
    }

}
