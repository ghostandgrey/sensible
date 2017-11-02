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
    public static final String MICROSECONDS = " μs";
    public static final String MILLIAMPS = " mA";
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
        TableLayout metadata = view.findViewById(R.id.metadata);
        metadata.setPadding(30, 30, 30, 30);
        metadata.addView(createTableRow("Vendor", sensor.getVendor()));
        metadata.addView(createTableRow("Name", sensor.getName()));
        metadata.addView(createTableRow("Type", sensor.getStringType() + " (" + sensor.getType() + ")"));
        metadata.addView(createTableRow("ID", "" + sensor.getId()));
        metadata.addView(createTableRow("FIFO Max", "" + sensor.getFifoMaxEventCount()));
        metadata.addView(createTableRow("FIFO Reserved", "" + sensor.getFifoReservedEventCount()));
        metadata.addView(createTableRow("Min Delay", "" + sensor.getMinDelay() + MICROSECONDS));
        metadata.addView(createTableRow("Max Delay", "" + sensor.getMaxDelay() + MICROSECONDS));
        metadata.addView(createTableRow("Max Range", "" + sensor.getMaximumRange()));
        metadata.addView(createTableRow("Power", sensor.getPower() + MILLIAMPS));
        metadata.addView(createTableRow("Reporting Mode", getReportingMode(sensor.getReportingMode())));
        metadata.addView(createTableRow("Add'l Info Supported", Boolean.toString(sensor.isAdditionalInfoSupported())));
        metadata.addView(createTableRow("Dynamic", Boolean.toString(sensor.isDynamicSensor())));
        metadata.addView(createTableRow("Wake Up", Boolean.toString(sensor.isWakeUpSensor())));

        TableLayout table = view.findViewById(R.id.value_table);
        for (int i = 0; i < 10; i++) {
            TableRow tableRow = new TableRow(view.getContext());
            tableRow.setVisibility(View.INVISIBLE);
            TextView label = new TextView(view.getContext());
            label.setText(i + ":");
            label.setTypeface(Typeface.DEFAULT_BOLD);
            label.setPadding(0, 0, 30, 0);
            tableRow.addView(label);
            TextView sensorValue = new TextView(view.getContext());
            sensorValue.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            sensorValues.add(sensorValue);
            tableRow.addView(sensorValue);
            table.addView(tableRow);
            tableRows.add(tableRow);
        }
        return view;
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
    private TableRow createTableRow(String attribute, String textValue) {
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
                sensorValueField.setText(new DecimalFormat("0.000").format(newValue));
            } else {
                tableRow.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {

    }
}
