package com.kaist.icg.pacman.manager;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;
import com.kaist.icg.pacman.tool.Material;

import java.nio.FloatBuffer;

/**
 * Manage level: spawn objects depending on the difficulty,
 * allocate/deallocate ground...
 */

public class ShaderManager {

    public enum Shader {
        TOON, PHONG, DIFFUSE, UI
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

    float[] modelViewMatrix = new float[16];
    float[] normalMatrix = new float[16];

    //OpenGL program
    private int program;
    private Shader currentShader = Shader.TOON;

    private ShaderManager() {}

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
            case UI:  fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "ui.fshader");
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
        int lightHandle = GLES20.glGetUniformLocation(program, "uLight");
        int ambientHandle = GLES20.glGetUniformLocation(program, "uAmbient");
        int diffuseHandle = GLES20.glGetUniformLocation(program, "uDiffuse");
        int specularHandle = GLES20.glGetUniformLocation(program, "uSpecular");
        int shininessHandle = GLES20.glGetUniformLocation(program, "uShininess");
        int colorHandle = GLES20.glGetUniformLocation(program, "uColor");
        int textureHandle = GLES20.glGetUniformLocation(program, "uTexture");

        GLES20.glUniform3fv(lightHandle, 1, lightPosition, 0);
        GLES20.glUniform3fv(ambientHandle, 1, material.getAmbientLight(), 0);
        GLES20.glUniform3fv(diffuseHandle, 1, material.getDiffuseLight(), 0);
        GLES20.glUniform3fv(specularHandle, 1, material.getSpecularLight(), 0);
        GLES20.glUniform1f(shininessHandle, material.getShininess());
        GLES20.glUniform3fv(colorHandle, 1, material.getColor(), 0);

        if(material.isTextured()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, material.getTextureDataHandler());
            GLES20.glUniform1i(textureHandle, 0);
        }
    }

    // load shaders, link matrices, variables and buffers, draw
    public void draw(float[] modelMatrix,
                     FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer,
                     int count, Material material, Shader shader) {
        if (shader != currentShader) {
            program = GLES20.glCreateProgram();
            loadVertexShader();
            loadFragmentShader(shader);
            GLES20.glLinkProgram(program);
            GLES20.glUseProgram(program);
        }

        currentShader = shader;

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
        int textureCoordinatesHandle = GLES20.glGetAttribLocation(program, "aTextureCoordinate");

        //Set attributes
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, 3,
                GLES20.GL_FLOAT, false,
                3*4, vertexBuffer);
        GLES20.glVertexAttribPointer(
                normalHandle, 3,
                GLES20.GL_FLOAT, false,
                3*4, normalBuffer);

        if(material.isTextured()) {
            GLES20.glEnableVertexAttribArray(textureCoordinatesHandle);
            GLES20.glVertexAttribPointer(
                    textureCoordinatesHandle, 2,
                    GLES20.GL_FLOAT, false,
                    2*4, textureCoordinatesBuffer);


        }

        linkMaterialVariables(material);

        //actual drawing
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count);

        // end draw
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);

        if(material.isTextured())
            GLES20.glDisableVertexAttribArray(textureCoordinatesHandle);
    }

    // draw with default shader
    public void draw(float[] modelMatrix,
                     FloatBuffer vertexBuffer, FloatBuffer normalBuffer, int count,
                     Material material) {
        draw(modelMatrix, vertexBuffer, normalBuffer, null, count,
                material, Shader.DIFFUSE);
    }
}
