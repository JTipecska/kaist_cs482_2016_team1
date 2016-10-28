package com.kaist.icg.pacman.tool;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;

// TODO: define materials like 'Ghost material' and pass it to the drawables.
public class Material {
    private float[] color = {1, 1, 1};
    //TODO: we might just set a costant ambientLight for the whole scene
    private float[] ambientLight = {0.1f, 0.1f, 0.1f};
    private float[] diffuseLight = {0.1f, 0.1f, 0.1f};
    private float[] specularLight = {0.1f, 0.1f, 0.1f};
    private float shininess = 0.1f;

    private boolean textured;
    private Bitmap textureBitmap;
    private int textureDataHandler;

    public Material(float[] color){
        this.color = color;
        this.textured = false;
    }

    public Material(String textureFile){
        this.textureBitmap = PacManGLRenderer.loadImage(textureFile);
        this.textured = true;

        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        // Set wrapping mode
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0);

        if (textureHandle[0] == 0)
            throw new RuntimeException("Error loading textureBitmap.");

        this.textureDataHandler = textureHandle[0];
        this.textureBitmap.recycle();
    }

    public Material(float[] color, float[] aLight, float[] dLight,
        float[] sLight, float shininess){
        this.color = color;
        this.ambientLight = aLight;
        this.diffuseLight = dLight;
        this.specularLight = sLight;
        this.shininess = shininess;
    }

    public Material(float[] aLight, float[] dLight,
                    float[] sLight, float shininess){
        this.ambientLight = aLight;
        this.diffuseLight = dLight;
        this.specularLight = sLight;
        this.shininess = shininess;
    }

    public float[] getColor(){return color;}
    public float[] getAmbientLight(){return ambientLight;}
    public float[] getDiffuseLight() {return diffuseLight;}
    public float[] getSpecularLight() {return specularLight;}
    public float getShininess() {return shininess;}
    public boolean isTextured() {return textured;}
    public int getTextureDataHandler() {return textureDataHandler;}

    public void setColor(float[] color) {this.color = color;}
}
