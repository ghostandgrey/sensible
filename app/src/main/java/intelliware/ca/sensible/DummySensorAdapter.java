package intelliware.ca.sensible;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DummySensorAdapter implements SensorAdapter {
    public static final String MICRO_SECONDS = " μs";
    public static final String MICRO_TESLA = " μT";
    public static final String MILLI_AMPS = " mA";
    public static final String METRES_PER_SECOND_SQUARED = " m/s^2";
    public static final String RADS_PER_SECOND = " rad/s";
    public static final String SI_LUX = "SI lux";
    public static final String MILLIBAR = " hPa";
    public static final String CM = " cm";
    public static final String DEGREES_CELCIUS = "°C";
    private View view = null;
    private Sensor sensor;
    private List<TextView> sensorValues = new ArrayList<>();
    private List<TableRow> tableRows = new ArrayList<>();

    public DummySensorAdapter(Sensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Resources resources) {
        view = inflater.inflate(R.layout.dummy_sensor, container, false);
        createMetadata();
        createValuesTable();
        return view;
    }

    private void createValuesTable() {
        TableLayout table = view.findViewById(R.id.value_table);
        for (int rowIndex = 0; rowIndex < 10; rowIndex++) {
            TableRow tableRow = new TableRow(view.getContext());
            tableRow.setVisibility(View.INVISIBLE);

            tableRow.addView(createSensorValueLabel(sensor.getType(), rowIndex));

            TextView sensorValue = new TextView(view.getContext());
            sensorValue.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

            sensorValues.add(sensorValue);
            tableRow.addView(sensorValue);
            table.addView(tableRow);
            tableRows.add(tableRow);
        }
    }

    @NonNull
    private TextView createSensorValueLabel(int sensorType, int rowIndex) {
        TextView label = new TextView(view.getContext());
        label.setText(getRowLabel(sensorType, rowIndex) + ":");
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setPadding(0, 0, 30, 0);
        return label;
    }

    private String getRowLabel(int sensorType, int rowIndex) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
            case Sensor.TYPE_MAGNETIC_FIELD:
                switch (rowIndex) {
                    case 0:
                        return "x";
                    case 1:
                        return "y";
                    case 2:
                        return "z";
                    default:
                        return "" + rowIndex;
                }
            default:
                return "" + rowIndex;

        }
    }

    private void createMetadata() {
        TableLayout metadata = view.findViewById(R.id.metadata);
        metadata.setPadding(30, 30, 30, 30);
        metadata.addView(createMetadataTableRow("Vendor", sensor.getVendor()));
        metadata.addView(createMetadataTableRow("Name", sensor.getName()));
        metadata.addView(createMetadataTableRow("Type", sensor.getStringType() + " (" + sensor.getType() + ")"));
        metadata.addView(createMetadataTableRow("ID", "" + sensor.getId()));
        metadata.addView(createMetadataTableRow("FIFO Max", "" + sensor.getFifoMaxEventCount()));
        metadata.addView(createMetadataTableRow("FIFO Reserved", "" + sensor.getFifoReservedEventCount()));
        metadata.addView(createMetadataTableRow("Min Delay", "" + sensor.getMinDelay() + MICRO_SECONDS));
        metadata.addView(createMetadataTableRow("Max Delay", "" + sensor.getMaxDelay() + MICRO_SECONDS));
        metadata.addView(createMetadataTableRow("Max Range", "" + sensor.getMaximumRange()));
        metadata.addView(createMetadataTableRow("Power", sensor.getPower() + MILLI_AMPS));
        metadata.addView(createMetadataTableRow("Reporting Mode", getReportingMode(sensor.getReportingMode())));
        metadata.addView(createMetadataTableRow("Add'l Info Supported", Boolean.toString(sensor.isAdditionalInfoSupported())));
        metadata.addView(createMetadataTableRow("Dynamic", Boolean.toString(sensor.isDynamicSensor())));
        metadata.addView(createMetadataTableRow("Wake Up", Boolean.toString(sensor.isWakeUpSensor())));
    }

    private String getReportingMode(int reportingMode) {
        switch (reportingMode) {
            case Sensor.REPORTING_MODE_CONTINUOUS:
                return "Continuous (" + reportingMode + ")";
            case Sensor.REPORTING_MODE_ON_CHANGE:
                return "On Change (" + reportingMode + ")";
            case Sensor.REPORTING_MODE_ONE_SHOT:
                return "One Shot (" + reportingMode + ")";
            case Sensor.REPORTING_MODE_SPECIAL_TRIGGER:
                return "Special Trigger (" + reportingMode + ")";
            default:
                return "Unknown";
        }
    }

    @NonNull
    private TableRow createMetadataTableRow(String attribute, String textValue) {
        TableRow row = new TableRow(view.getContext());
        TextView label = new TextView(view.getContext());
        label.setText(attribute + ": ");
        row.addView(label);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        TextView textView = new TextView(view.getContext());
        textView.setText(textValue);
        textView.setPadding(30, 0, 0, 0);
        row.addView(textView);
        return row;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (view == null) {
            return;
        }
        float[] newValues = sensorEvent.values;
        for (int i = 0; i < sensorValues.size(); i++) {
            TextView sensorValueField = this.sensorValues.get(i);
            TableRow tableRow = tableRows.get(i);
            if (i < newValues.length) {
                float newValue = newValues[i];
                tableRow.setVisibility(View.VISIBLE);
                sensorValueField.setText(new DecimalFormat("0.000").format(newValue) + getUnits(sensorEvent.sensor.getType(), i));
            } else {
                tableRow.setVisibility(View.INVISIBLE);
            }
        }
    }

    private String getUnits(int sensorType, int i) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return METRES_PER_SECOND_SQUARED;
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return MICRO_TESLA;
            case Sensor.TYPE_GYROSCOPE:
                return RADS_PER_SECOND;
            case Sensor.TYPE_LIGHT:
                return SI_LUX;
            case Sensor.TYPE_PRESSURE:
                return MILLIBAR;
            case Sensor.TYPE_PROXIMITY:
                return CM;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return DEGREES_CELCIUS;
            default:
                return "";
        }
    }

    @Override
    public void onDestroy() {

    }
}
