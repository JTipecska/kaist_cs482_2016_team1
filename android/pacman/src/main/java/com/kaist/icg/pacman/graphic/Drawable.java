package com.kaist.icg.pacman.graphic;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Drawable object class.
 * Every object that can be draw should extend this class
 */
public class Drawable {
    // Raw transformation data
    private float[] position;
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
    protected float[] temp = new float[16];

    //OpenGL
    protected int program;
    protected int programOutline;
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

    //Test
    protected Drawable parent;
    protected ArrayList<Drawable> children;

    public Drawable() {
        position = new float[3];
        scale = new float[] {1f, 1f, 1f};
        rotationAxis = new float[] {0, 1f, 0};

        modelMatrix = new float[16];
        translationMatrix = new float[16];
        scaleMatrix = new float[16];
        rotationMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0, position[0], position[1], position[2]);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale[0], scale[1], scale[2]);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, rotationAngle, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
        computeModelMatrix();

        children = new ArrayList<>();

        //TODO: set default OpenGL program
    }

    public void draw(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUseProgram(programOutline);

        prepareDraw(projectionMatrix, viewMatrix);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexBufferSize);

        endDraw();

        for(Drawable child : children)
            child.draw(projectionMatrix, viewMatrix);
    }

    /**
     * Compute view model matrix and send it with position and normal to the shader
     * @param projectionMatrix
     * @param viewMatrix
     */
    protected void prepareDraw(float[] projectionMatrix, float[] viewMatrix) {
        //it seems to work just binding the variables to program and not also to programOutline
        //but maybe this need to be fixed?

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

    public void translate(float x, float y, float z) {
        position[0] += x;
        position[1] += y;
        position[2] += z;

        //Now recalculate the transition matrix
        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0, position[0], position[1], position[2]);
    }

    public void setPosition(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;

        //Now recalculate the transition matrix
        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0, position[0], position[1], position[2]);
    }

    public void setScale(float x, float y, float z) {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;

        //Now recalculate the setScale matrix
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale[0], scale[1], scale[2]);
    }

    public void scale(float x, float y, float z) {
        scale[0] *= x;
        scale[1] *= y;
        scale[2] *= z;

        //Now recalculate the setScale matrix
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale[0], scale[1], scale[2]);
    }

    public void setRotation(float x, float y, float z, float angle) {
        rotationAxis[0] = x;
        rotationAxis[1] = y;
        rotationAxis[2] = z;
        rotationAngle = angle;

        //Now recalculate the rotation matrix
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, rotationAngle, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
    }

    //TODO: compute new rotation but combining the old one and the new one
    /*public void rotate(float x, float y, float z, float angle) {
    }*/

    /**
     * Compute the model matrix from setScale matrix, rotation matrix and position matrix
     */
    protected void computeModelMatrix() {
        if(parent == null)
            Matrix.setIdentityM(modelMatrix, 0);
        else
            System.arraycopy(parent.modelMatrix, 0, modelMatrix, 0, 16);

        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, translationMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotationMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0);
    }

    protected void normalMatrix(float[] dst, int dstOffset, float[] src, int srcOffset) {
        Matrix.invertM(dst, dstOffset, src, srcOffset);
        dst[12] = 0;
        dst[13] = 0;
        dst[14] = 0;

        System.arraycopy(dst, 0, temp, 0, 16);

        Matrix.transposeM(dst, dstOffset, temp, 0);
    }

    public float[] getPosition() {
        return position;
    }

    /**
     * Add a new child to this node in the scene graph
     * @param child
     */
    public void addChild(Drawable child) {
        if(child.parent != null)
            child.parent.children.remove(child);

        children.add(child);
        child.parent = this;
    }
}
