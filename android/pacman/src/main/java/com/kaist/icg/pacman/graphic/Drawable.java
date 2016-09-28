package com.kaist.icg.pacman.graphic;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Drawable object class.
 * Every object that can be draw should extend this class
 */
public class Drawable {
    // Raw transformation data
    private float[] translation;
    private float[] scale;
    private float[] rotationAxis;
    private float rotationAngle;

    //Matrix
    protected float[] modelMatrix;
    protected float[] translationMatrix;
    protected float[] scaleMatrix;
    protected float[] rotationMatrix;
    protected float[] modelViewMatrix = new float[16];
    protected float[] normalMatrix = new float[16];

    //OpenGL
    protected int program;
    protected int vertexBufferSize;
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer normalBuffer;

    // Handler
    protected int positionHandle;
    protected int normalHandle;
    protected int projectionMatrixHandle;
    protected int modelViewMatrixHandle;
    protected int normalMatrixHandle;

    // Const
    protected static final int COORDS_PER_VERTEX = 3;
    protected static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    public Drawable() {
        translation = new float[3];
        scale = new float[] {1f, 1f, 1f};
        rotationAxis = new float[] {0, 1f, 0};

        modelMatrix = new float[16];
        translationMatrix = new float[16];
        scaleMatrix = new float[16];
        rotationMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0, translation[0], translation[1], translation[2]);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale[0], scale[1], scale[2]);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, rotationAngle, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
        computeModelMatrix();

        //TODO: set default OpenGL program
    }

    public void draw(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUseProgram(program);

        //Apply specific transformation here by modifying the model matrix (in a new class)

        prepareDraw(projectionMatrix, viewMatrix);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexBufferSize);

        endDraw();
    }

    /**
     * Compute view model matrix and send it with position and normal to the shader
     * @param projectionMatrix
     * @param viewMatrix
     */
    protected void prepareDraw(float[] projectionMatrix, float[] viewMatrix) {
        computeModelMatrix();

        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        normalMatrix(normalMatrix, 0, modelViewMatrix, 0);

        //Retrieve uniforms handlers
        projectionMatrixHandle = GLES20.glGetUniformLocation(program, "uProjMatrix");
        modelViewMatrixHandle = GLES20.glGetUniformLocation(program, "uModelViewMatrix");
        normalMatrixHandle = GLES20.glGetUniformLocation(program, "uNormalMatrix");

        //Set uniforms
        GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(modelViewMatrixHandle, 1, false, modelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(normalMatrixHandle, 1, false, normalMatrix, 0);

        //Retrieve attributes handlers
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        normalHandle = GLES20.glGetAttribLocation(program, "aNormal");

        //Set attributes
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, vertexBuffer);
        GLES20.glVertexAttribPointer(
                normalHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, normalBuffer);
    }

    protected void endDraw() {
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
    }

    public void translateFromLocal(float x, float y, float z) {
        translation[0] += x;
        translation[1] += y;
        translation[2] += z;

        //Now recalculate the transition matrix
        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0, translation[0], translation[1], translation[2]);
    }

    public void translate(float x, float y, float z) {
        translation[0] = x;
        translation[1] = y;
        translation[2] = z;

        //Now recalculate the transition matrix
        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0, translation[0], translation[1], translation[2]);
    }

    public void scale(float x, float y, float z) {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;

        //Now recalculate the scale matrix
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale[0], scale[1], scale[2]);
    }

    public void scaleFromLocal(float x, float y, float z) {
        scale[0] *= x;
        scale[1] *= y;
        scale[2] *= z;

        //Now recalculate the scale matrix
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale[0], scale[1], scale[2]);
    }

    public void rotate(float x, float y, float z, float angle) {
        rotationAxis[0] = x;
        rotationAxis[1] = y;
        rotationAxis[2] = z;
        rotationAngle = angle;

        //Now recalculate the rotation matrix
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, rotationAngle, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
    }

    /**
     * Compute the model matrix from scale matrix, rotation matrix and translation matrix
     */
    protected void computeModelMatrix() {
        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, translationMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotationMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0);
    }

    protected void normalMatrix(float[] dst, int dstOffset, float[] src, int srcOffset) {
        Matrix.invertM(dst, dstOffset, src, srcOffset);
        dst[12] = 0;
        dst[13] = 0;
        dst[14] = 0;

        float[] temp = Arrays.copyOf(dst, 16);

        Matrix.transposeM(dst, dstOffset, temp, 0);
    }
}
