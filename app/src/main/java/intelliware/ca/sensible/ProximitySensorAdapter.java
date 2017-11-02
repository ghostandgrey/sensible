package intelliware.ca.sensible;

import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.SensorEvent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sccomponents.gauges.ScCopier;
import com.sccomponents.gauges.ScGauge;
import com.sccomponents.gauges.ScLinearGauge;
import com.sccomponents.gauges.ScNotches;
import com.sccomponents.gauges.ScPointer;
import com.sccomponents.gauges.ScWriter;

public class ProximitySensorAdapter implements SensorAdapter {

    private View view = null;

    private Thread audioThread;
    private int sampleRate = 44100;
    private boolean isRunning = true;
    private float frequency = 0;
//    private CheckBox theraminOn;

    public ProximitySensorAdapter() {
        audioThread = createAudioThread();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Resources resources) {
        view = inflater.inflate(R.layout.proximity_sensor, container, false);
        setUpView(view, resources);
        audioThread.start();
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (view == null) {
            return;
        }
        float[] newValues = sensorEvent.values;
        float value = sensorEvent.values[0];
        view.<TextView>findViewById(R.id.proximity).setText("" + value + " cm");
        view.<ScLinearGauge>findViewById(R.id.proximityGauge).setHighValue(value * 10);
        frequency = value;
    }

    private void setUpView(View view, Resources resources) {
        // Find the components
        final ScLinearGauge gauge = view.findViewById(R.id.proximityGauge);
        assert gauge != null;

        // Set the last token on the end of path
        final ScWriter writer = (ScWriter) gauge.findFeature(ScGauge.WRITER_IDENTIFIER);
        writer.setLastTokenOnEnd(true);

        // Set the value
        gauge.setHighValue(0);

        // Before draw
        gauge.setOnDrawListener(new ScGauge.OnDrawListener() {
            @Override
            public void onBeforeDrawCopy(ScCopier.CopyInfo info) {
                // NOP
            }

            @Override
            public void onBeforeDrawNotch(ScNotches.NotchInfo info) {
                // The notch length
                info.length = gauge.dipToPixel(info.index % 4 == 0 ? 20 : 10);
            }

            @Override
            public void onBeforeDrawPointer(ScPointer.PointerInfo info) {
                // NOP
            }

            @Override
            public void onBeforeDrawToken(ScWriter.TokenInfo info) {
                // Get the text bounds
                Rect bounds = new Rect();
                info.source.getPainter().getTextBounds(info.text, 0, info.text.length(), bounds);

                // Zero angle
                info.angle = 0.0f;
                info.offset.x = -50 - bounds.width();
                info.offset.y = bounds.height() / 2;
            }
        });
    }

    private Thread createAudioThread() {

        Thread audioThread = new Thread() {

            public void run() {
                // set process priority
                setPriority(Thread.MAX_PRIORITY);

                int buffsize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                // create an audiotrack object
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        buffsize,
                        AudioTrack.MODE_STREAM);

                short samples[] = new short[buffsize];
                int amp = 10000;
                double twopi = 8. * Math.atan(1.);
                double fr = 0;
                double ph = 0.0;

                audioTrack.play();

                while (isRunning) {

                    fr = 440 * frequency;
                    for (int i = 0; i < buffsize; i++) {
                        samples[i] = (short) (amp * Math.sin(ph));
                        ph += twopi * fr / sampleRate;
                    }
                    audioTrack.write(samples, 0, buffsize);
                }

                audioTrack.stop();
                audioTrack.release();
            }
        };

        return audioThread;
    }

    @Override
    public void onDestroy() {

        isRunning = false;
        try {
            audioThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        audioThread = null;
    }
}
