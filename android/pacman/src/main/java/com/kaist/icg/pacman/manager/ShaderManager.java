package com.kaist.icg.pacman.manager;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;
import com.kaist.icg.pacman.tool.Material;

/**
 * Manage level: spawn objects depending on the difficulty,
 * allocate/deallocate ground...
 */

public class ShaderManager {

    public enum Shader {
        TOON, PHONG, DIFFUSE
    }

    //Singleton
    private static ShaderManager INSTANCE;

    public static  ShaderManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ShaderManager();

        return INSTANCE;
    }

    private float[] projectionMatrix;
    private float[] viewMatrix;
    private float[] lightPosition;

    //OpenGL program
    private int program;
    private Shader currentShader = Shader.TOON;

    //TODO: we want to retrieve this information from the drawable
    protected static final int COORDS_PER_VERTEX = 3;
    protected static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    private ShaderManager() {

    }

    public void initialize(float[] projectionMatrix, float[] viewMatrix,
                           float[] lightPosition) {
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
        this.lightPosition = lightPosition;

        program = GLES20.glCreateProgram();
        loadVertexShader();
        loadFragmentShader(currentShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);
    }

    public void loadVertexShader() {
        int vertexShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_VERTEX_SHADER, "basic-gl2.vshader");
        GLES20.glAttachShader(program, vertexShader);
    }

    public void loadFragmentShader(Shader shader){
        int fragmentShader = 0;
        switch(shader){
            case TOON:  fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "toon-gl2.fshader");
                break;
            case PHONG:  fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "phong-gl2.fshader");
                break;
            case DIFFUSE:  fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "diffuse-gl2.fshader");
                break;
            default:  fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "diffuse-gl2.fshader");
                break;
        }
        GLES20.glAttachShader(program, fragmentShader);
    }


    // link all the material specific variables with the shader
    public void linkMaterialVariables(Material material){
        // uniforms
        int colorHandle = GLES20.glGetUniformLocation(program, "uColor");
        int lightHandle = GLES20.glGetUniformLocation(program, "uLight");
        int ambientHandle = GLES20.glGetUniformLocation(program, "uAmbient");
        int diffuseHandle = GLES20.glGetUniformLocation(program, "uDiffuse");
        int specularHandle = GLES20.glGetUniformLocation(program, "uSpecular");
        int shininessHandle = GLES20.glGetUniformLocation(program, "uShininess");
        GLES20.glUniform3fv(colorHandle, 1, material.getColor(), 0);
        GLES20.glUniform3fv(lightHandle, 1, lightPosition, 0);
        GLES20.glUniform3fv(ambientHandle, 1, material.getAmbientLight(), 0);
        GLES20.glUniform3fv(diffuseHandle, 1, material.getDiffuseLight(), 0);
        GLES20.glUniform3fv(specularHandle, 1, material.getSpecularLight(), 0);
        GLES20.glUniform1f(shininessHandle, material.getShininess());
    }

    // load shaders, link matrices, variables and buffers, draw
    public void draw(float[] modelMatrix,
                     FloatBuffer vertexBuffer, FloatBuffer normalBuffer, int count,
                     Material material, Shader shader) {
        if (shader != currentShader) {
            program = GLES20.glCreateProgram();
            loadVertexShader();
            loadFragmentShader(shader);
            GLES20.glLinkProgram(program);
            GLES20.glUseProgram(program);
        }

        currentShader = shader;

        float[] modelViewMatrix = new float[16];
        float[] normalMatrix = new float[16];

        // compute model view matrix
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // compute normal matrix
        Matrix.invertM(normalMatrix, 0, modelViewMatrix, 0);
        normalMatrix[12] = 0;
        normalMatrix[13] = 0;
        normalMatrix[14] = 0;
        float[] temp = new float[16];
        System.arraycopy(normalMatrix, 0, temp, 0, 16);
        Matrix.transposeM(normalMatrix, 0, temp, 0);

        //Retrieve uniforms handlers
        int projectionMatrixHandle = GLES20.glGetUniformLocation(program, "uProjMatrix");
        int modelViewMatrixHandle = GLES20.glGetUniformLocation(program, "uModelViewMatrix");
        int normalMatrixHandle = GLES20.glGetUniformLocation(program, "uNormalMatrix");

        //Set uniforms
        GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(modelViewMatrixHandle, 1, false, modelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(normalMatrixHandle, 1, false, normalMatrix, 0);

        //Retrieve attributes handlers
        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        int normalHandle = GLES20.glGetAttribLocation(program, "aNormal");

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

        linkMaterialVariables(material);

        //actual drawing
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count);

        // end draw
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
    }

    // draw with default shader
    public void draw(float[] modelMatrix,
                     FloatBuffer vertexBuffer, FloatBuffer normalBuffer, int count,
                     Material material) {
        draw(modelMatrix, vertexBuffer, normalBuffer, count,
                material, Shader.DIFFUSE);
    }

}
