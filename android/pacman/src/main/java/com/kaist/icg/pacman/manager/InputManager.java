package com.kaist.icg.pacman.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.kaist.icg.pacman.graphic.android.PacManActivity;

/**
 * Handle user input (gyroscope, touch...)
 */
public class InputManager implements SensorEventListener{

    //Singleton
    private static InputManager INSTANCE;

    public static InputManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new InputManager();

        return INSTANCE;
    }

    //TODO: tune this parameter
    private static final float MAX_HORIZONTAL_MOVEMENT_SPEED = 1;

    private float horizontalMovement;

    //Sensor stuff
    private SensorManager sensorManager;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private double roll = 0;
    private ITouchListener touchListener;

    private InputManager() {
        sensorManager = (SensorManager) PacManActivity.context.getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Called on every frame
     * @param timeElapsed time elapsed since last update (in ms)
     */
    public void update(float timeElapsed) {
        //TODO: do something with horizontalMovement and isJumping (move objects...)
        //System.out.println(horizontalMovement + "\t\t" + isJumping + "\t\t" + timeElapsed);
    }

    /**
     * OnPause: disable gyroscope update
     */
    public void onPause() {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    /**
     * OnResume: enable gyroscope update
     */
    public void onResume() {
        System.out.println("resume");
        mLastAccelerometerSet = false;
        mLastMagnetometerSet = false;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Called when user tilt the phone
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            //mOrientation: [azimuth, pitch, roll]
            roll = mOrientation[2];

            //TODO: smooth movement speed
            horizontalMovement = (float) (roll * 5);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Called when user start touching the screen (see {@link com.kaist.icg.pacman.graphic.android.PacManGLSurfaceView})
     * @param x current X touch position
     * @param y current Y touch position
     */
    public void onTouchStart(float x, float y) {
        if(touchListener != null)
            touchListener.onTouchStart(x, y);
    }

    /**
     * Called when user stop touching the screen (see {@link com.kaist.icg.pacman.graphic.android.PacManGLSurfaceView})
     * @param x current X touch position
     * @param y current Y touch position
     */
    public void onTouchEnd(float x, float y) {
        if(touchListener != null)
            touchListener.onTouchEnd(x, y);
    }

    public float getHorizontalMovement() {
        return horizontalMovement;
    }

    public void setTouchListener(ITouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public interface ITouchListener {
        void onTouchStart(float x, float y);
        void onTouchEnd(float x, float y);
    }
}
