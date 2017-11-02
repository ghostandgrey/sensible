package intelliware.ca.sensible;

import android.content.res.Resources;
import android.hardware.SensorEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LightSensorAdapter implements SensorAdapter {
    private static final int START_RANGE = -20;
    private static final int END_RANGE = 30;
    private static final int NUMBER_OF_TOKENS = 9;
    private static final int BREAK_RANGE = (END_RANGE - START_RANGE) / NUMBER_OF_TOKENS;

    private View view = null;

    public LightSensorAdapter() {
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Resources resources) {
        view = inflater.inflate(R.layout.light_sensor, container, false);
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (view == null) {
            return;
        }
        float[] newValues = sensorEvent.values;
        float value = sensorEvent.values[0];
        view.<TextView>findViewById(R.id.light).setText("" + value);

        int imageNumber = (int) value / 14;

        int imageId;
        switch (imageNumber) {
            case 1:
                imageId = R.drawable.ic_brightness_1_black_48dp;
                break;
            case 2:
                imageId = R.drawable.ic_brightness_2_grey_600_48dp;
                break;
            case 3:
                imageId = R.drawable.ic_brightness_3_grey_400_48dp;
                break;
            case 4:
                imageId = R.drawable.ic_brightness_4_deep_orange_700_48dp;
                break;
            case 5:
                imageId = R.drawable.ic_brightness_5_deep_orange_300_48dp;
                break;
            case 6:
                imageId = R.drawable.ic_brightness_6_yellow_600_48dp;
                break;
            case 7:
                imageId = R.drawable.ic_brightness_7_yellow_300_48dp;
                break;
            default:
                imageId = R.drawable.ic_brightness_1_black_48dp;
        }
        ImageView image = view.findViewById(R.id.brightnessImage);
        image.setImageResource(imageId);

    }

    @Override
    public void onDestroy() {

    }

}
