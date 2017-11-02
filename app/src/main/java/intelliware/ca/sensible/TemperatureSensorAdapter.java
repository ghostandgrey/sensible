package intelliware.ca.sensible;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.SensorEvent;
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

public class TemperatureSensorAdapter implements SensorAdapter {
    private static final int START_RANGE = -12;
    private static final int END_RANGE = 28;
    private static final int NUMBER_OF_TOKENS = 9;
    private static final int BREAK_RANGE = (END_RANGE - START_RANGE) / NUMBER_OF_TOKENS;

    private View view = null;

    public TemperatureSensorAdapter() {
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Resources resources) {
        view = inflater.inflate(R.layout.temperature_sensor, container, false);
        setUpView(view, resources);
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (view == null) {
            return;
        }
        float[] newValues = sensorEvent.values;
        float value = sensorEvent.values[0];
        view.<TextView>findViewById(R.id.temperature).setText("" + value);
        view.<TextView>findViewById(R.id.temperature_counter).setText(value + "°C");
        view.<ScArcGauge>findViewById(R.id.temperature_gauge).setHighValue(value, START_RANGE, END_RANGE);
    }

    @Override
    public void onDestroy() {

    }

    private void setUpView(View view, Resources resources) {
        // Find the components
        final ScArcGauge gauge = this.view.findViewById(R.id.temperature_gauge);
        assert gauge != null;

        final TextView counter = this.view.findViewById(R.id.temperature_counter);
        assert counter != null;

        // Create a drawable
        final Bitmap indicator = BitmapFactory.decodeResource(resources, R.drawable.indicator);

        // Set the values.
        gauge.setHighValue(0, START_RANGE, END_RANGE);
//        gauge.setPathTouchThreshold(40);
        gauge.setRecognizePathTouch(false);

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
        String[] tokens = new String[NUMBER_OF_TOKENS];
        for (int index = 0; index < NUMBER_OF_TOKENS; index++) {
            tokens[index] = Integer.toString(index * 5 - 10);
        }
        gauge.setTextTokens(tokens);

        ScWriter writer = (ScWriter) gauge.findFeature(ScGauge.WRITER_IDENTIFIER);
        writer.setLastTokenOnEnd(true);

        gauge.setOnEventListener(new ScGauge.OnEventListener() {
            @Override
            public void onValueChange(float lowValue, float highValue) {
                // Write the value
                highValue = ScGauge.percentageToValue(highValue, START_RANGE, END_RANGE);
                float round = (Math.round(highValue * 10.0f)) / 10.0f;
                counter.setText(Float.toString(round) + "°");
            }
        });

        gauge.setOnDrawListener(new ScGauge.OnDrawListener() {
            @Override
            public void onBeforeDrawCopy(ScCopier.CopyInfo info) {
                // Check if is the progress
                if (ScGauge.PROGRESS_IDENTIFIER.equals(info.source.getTag())) {
                    // Scale and adjust the offset
                    info.scale = new PointF(1.1f, 1.1f);
                    info.offset = new PointF(-65, -65);

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
                if (ScGauge.HIGH_POINTER_IDENTIFIER.equals(info.source.getTag())) {
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
