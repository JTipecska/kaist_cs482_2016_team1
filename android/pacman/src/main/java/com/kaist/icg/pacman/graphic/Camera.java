package com.kaist.icg.pacman.graphic;

import android.opengl.Matrix;

public class Camera {
    private static Camera INSTANCE;

    public static Camera getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Camera();

        return INSTANCE;
    }

    private int screenWidth;
    private int screenHeight;
    private float[] mProjMatrix = new float[16];
    private float[] mViewMatrix = new float[16];

    //Frustum
    private float frustumRatio;
    private float frustumLeft;
    private float frustumRight;
    private float frustumBottom = -1.0f;
    private float frustumTop = 1.0f;
    private float frustumNear = 1f;
    private float frustumFar = 20.0f;

    // Position the eye behind the origin.
    private float eyeX = 0.0f;
    private float eyeY = 0.0f;
    private float eyeZ = 4f;

    // We are looking toward the distance
    private float lookX = 0.0f;
    private float lookY = 0.0f;
    private float lookZ = -1.0f;

    // Set our up vector. This is where our head would be pointing were we holding the camera.
    private float upX = 0.0f;
    private float upY = 1.0f;
    private float upZ = 0.0f;

    public Camera() {

    }

    public void onSurfaceChanged(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect frustumRatio.
        frustumRatio = (float) width / height;
        frustumLeft = -frustumRatio;
        frustumRight = frustumRatio;

        Matrix.frustumM(mProjMatrix, 0, frustumLeft, frustumRight, frustumBottom, frustumTop, frustumNear, frustumFar);
    }

    public void resetViewMatrix() {
        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    public float[] getProjMatrix() {
        return mProjMatrix;
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }

    public float getNearestZPosition() {
        return eyeZ - frustumNear - 0.001f;
    }

    public float screenToCameraX(float x) {
        return ((x/screenWidth) * (frustumRatio*2)) - frustumRatio;
    }

    public float screenToCameraY(float y) {
        return 0 - (((y/screenHeight) * 2) - 1);
    }

    public float screenWidthToScaleX(float width) {
        return (width / screenWidth) * (frustumRatio * 2);
    }

    public float screenHeightToScaleY(float height) {
        return (height / screenHeight) * 2;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
