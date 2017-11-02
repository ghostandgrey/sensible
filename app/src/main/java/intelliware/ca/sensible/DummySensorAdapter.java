package intelliware.ca.sensible;

import android.content.res.Resources;
import android.graphics.Typeface;
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
    private List<TableRow> tableRows = new ArrayList<>();

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
            label.setTypeface(Typeface.DEFAULT_BOLD);
            label.setPadding(0, 0, 30, 0);
            tableRow.addView(label);
            TextView sensorValue = new TextView(view.getContext());
            sensorValues.add(sensorValue);
            tableRow.addView(sensorValue);
            table.addView(tableRow);
            tableRows.add(tableRow);
        }
        return view;
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
}
