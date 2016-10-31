package com.kaist.icg.pacman.graphic;

import android.opengl.Matrix;

import com.kaist.icg.pacman.manager.ShaderManager;
import com.kaist.icg.pacman.tool.Material;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * Drawable object class.
 * Every object that can be draw should extend this class
 */
public class Drawable {
    // shader Manager instance for drawing the object
    protected ShaderManager shaderManager;

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

    //OpenGL
    protected int vertexBufferSize;
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer normalBuffer;
    protected FloatBuffer textureCoordinatesBuffer;
    protected ShortBuffer drawOrderBuffer;


    //Test
    protected Drawable parent;
    public ArrayList<Drawable> children;


    protected Material material;
    protected ShaderManager.Shader shader = ShaderManager.Shader.TOON;

    public void setShader(ShaderManager.Shader shader) {this.shader = shader;}

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
        material = new Material(new float[]{0.5f, 0.5f, 0.0f});

        shaderManager = ShaderManager.getInstance();
        //TODO: set default OpenGL program
    }

    public void draw() {
        computeModelMatrix();

        //shaderManager.draw(modelMatrix, vertexBuffer,
        //        normalBuffer, vertexBufferSize,
        //        material, shader);

        for (Drawable child : children)
            child.draw();
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
