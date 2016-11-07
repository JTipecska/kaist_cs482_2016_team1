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
        DIFFUSE, TOON, PHONG, UI, DIFFUSETEX, TOONTEX
    }

    //Singleton
    private static ShaderManager INSTANCE;

    public static  ShaderManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ShaderManager();

        return INSTANCE;
    }

    private int shaderPrograms = 6;

    private float[] projectionMatrix;
    private float[] viewMatrix;
    private float[] lightPosition;

    float[] modelViewMatrix = new float[16];
    float[] normalMatrix = new float[16];

    //OpenGL program
    private int[] programs;
    private Shader currentShader = Shader.TOON;
    private int currentProgram = 0;

    private boolean initialized = false;

    private ShaderManager() {}

    public void initialize(float[] projectionMatrix, float[] viewMatrix,
                           float[] lightPosition) {
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
        this.lightPosition = lightPosition;

        if(!initialized) {
            programs = new int[shaderPrograms];

            int vertexShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_VERTEX_SHADER, "shader/basic-gl2.vshader");

            for (int i = 0; i < shaderPrograms; ++i) {
                programs[i] = GLES20.glCreateProgram();
                GLES20.glAttachShader(programs[i], vertexShader);
            }


            int fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "shader/diffuse-gl2.fshader");
            GLES20.glAttachShader(programs[0], fragmentShader);

            fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "shader/toon-gl2.fshader");
            GLES20.glAttachShader(programs[1], fragmentShader);

            fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "shader/phong-gl2.fshader");
            GLES20.glAttachShader(programs[2], fragmentShader);

            fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "shader/ui.fshader");
            GLES20.glAttachShader(programs[3], fragmentShader);

            fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "shader/diffuseTex-gl2.fshader");
            GLES20.glAttachShader(programs[4], fragmentShader);

            fragmentShader = PacManGLRenderer.loadShaderFromFile(
                    GLES20.GL_FRAGMENT_SHADER, "shader/toonTex-gl2.fshader");
            GLES20.glAttachShader(programs[5], fragmentShader);

            for (int i = 0; i < shaderPrograms; ++i) {
                GLES20.glLinkProgram(programs[i]);
            }

            GLES20.glUseProgram(programs[0]);

            this.initialized = true;
        }
    }

    // link all the material specific variables with the shader
    public void linkMaterialVariables(Material material, int program){
        // uniforms
        int opacityHandle = GLES20.glGetUniformLocation(program, "uOpacity");
        int lightHandle = GLES20.glGetUniformLocation(program, "uLight");
        int ambientHandle = GLES20.glGetUniformLocation(program, "uAmbient");
        int diffuseHandle = GLES20.glGetUniformLocation(program, "uDiffuse");
        int specularHandle = GLES20.glGetUniformLocation(program, "uSpecular");
        int shininessHandle = GLES20.glGetUniformLocation(program, "uShininess");
        int colorHandle = GLES20.glGetUniformLocation(program, "uColor");
        int textureHandle = GLES20.glGetUniformLocation(program, "uTexture");

        GLES20.glUniform1f(opacityHandle, material.getOpacity());
        GLES20.glUniform3fv(lightHandle, 1, lightPosition, 0);
        GLES20.glUniform3fv(ambientHandle, 1, material.getAmbientLight(), 0);
        GLES20.glUniform3fv(diffuseHandle, 1, material.getDiffuseLight(), 0);
        GLES20.glUniform3fv(specularHandle, 1, material.getSpecularLight(), 0);
        GLES20.glUniform1f(shininessHandle, material.getShininess());
        GLES20.glUniform3fv(colorHandle, 1, material.getColor(), 0);

        if(material.isTextured()) {
            GLES20.glActiveTexture(material.getTextureBloc());
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, material.getTextureDataHandler());
            GLES20.glUniform1i(textureHandle, 0);
        }
    }

    // load shaders, link matrices, variables and buffers, draw
    public void draw(float[] modelMatrix,
                     FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer,
                     int count, Material material, Shader shader) {

        if (shader != currentShader) {
            switch (shader) {
                case DIFFUSE:
                    GLES20.glUseProgram(programs[0]);
                    currentProgram = programs[0];
                    break;
                case TOON:
                    GLES20.glUseProgram(programs[1]);
                    currentProgram = programs[1];
                    break;
                case PHONG:
                    GLES20.glUseProgram(programs[2]);
                    currentProgram = programs[2];
                    break;
                case UI:
                    GLES20.glUseProgram(programs[3]);
                    currentProgram = programs[3];
                    break;
                case DIFFUSETEX:
                    GLES20.glUseProgram(programs[4]);
                    currentProgram = programs[4];
                    break;
                case TOONTEX:
                    GLES20.glUseProgram(programs[5]);
                    currentProgram = programs[5];
                    break;
                default:
                    GLES20.glUseProgram(programs[0]);
                    currentProgram = programs[0];
                    break;
            }
            currentShader = shader;
        }

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
        int projectionMatrixHandle = GLES20.glGetUniformLocation(currentProgram, "uProjMatrix");
        int modelViewMatrixHandle = GLES20.glGetUniformLocation(currentProgram, "uModelViewMatrix");
        int normalMatrixHandle = GLES20.glGetUniformLocation(currentProgram, "uNormalMatrix");

        //Set uniforms
        GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(modelViewMatrixHandle, 1, false, modelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(normalMatrixHandle, 1, false, normalMatrix, 0);

        //Retrieve attributes handlers
        int positionHandle = GLES20.glGetAttribLocation(currentProgram, "aPosition");
        int normalHandle = GLES20.glGetAttribLocation(currentProgram, "aNormal");
        int textureCoordinatesHandle = GLES20.glGetAttribLocation(currentProgram, "aTextureCoordinate");

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

        linkMaterialVariables(material, currentProgram);

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
