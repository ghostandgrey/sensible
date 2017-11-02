package intelliware.ca.sensible;

import android.content.res.Resources;
import android.hardware.SensorEvent;
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
    private View view = null;
    private List<TextView> sensorValues = new ArrayList<>();

    public DummySensorAdapter() {
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Resources resources) {
        view = inflater.inflate(R.layout.dummy_sensor, container, false);
        TableLayout table = view.findViewById(R.id.value_table);
        for (int i = 0; i < 10; i++) {
            TableRow tableRow = new TableRow(view.getContext());
            TextView label = new TextView(view.getContext());
            label.setText(i + ":");
            tableRow.addView(label);
            TextView sensorValue = new TextView(view.getContext());
            sensorValue.setText("FOO");
            sensorValues.add(sensorValue);
            tableRow.addView(sensorValue);
            table.addView(tableRow);
        }
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (view == null) {
            return;
        }
        float[] newValues = sensorEvent.values;
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
