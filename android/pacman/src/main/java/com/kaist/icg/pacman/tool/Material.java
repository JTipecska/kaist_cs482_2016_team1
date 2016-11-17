package com.kaist.icg.pacman.tool;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;
import com.kaist.icg.pacman.manager.TextureManager;

// TODO: define materials like 'Ghost material' and pass it to the drawables.
public class Material {

    //TODO: we might just set a costant ambientLight for the whole scene
    private float ambientIntensity = 0.1f;
    private float[] diffuseColor = {0.5f, 0.0f, 0.0f};
    private float[] specularColor = {0.2f, 0.2f, 0.2f};
    private float shininess = 16.0f;
    private float opacity = 1.0f;

    private boolean textured;
    private Bitmap textureBitmap;
    private int textureDataHandler;
    private int textureBloc;
    private TextureManager.TextureInfo textureInfo;

    public Material() {
    }

    public Material(float[] color) {
        this.diffuseColor = color;
        this.textured = false;
    }

    public Material(String textureFile) {
        loadTexture(PacManGLRenderer.loadImage(textureFile));
    }

    public Material(Bitmap texture) {
        loadTexture(texture);
    }

    private void loadTexture(Bitmap texture) {
        this.textureBitmap = texture;
        this.textured = true;

        if (textureInfo == null) {
            textureInfo = TextureManager.getInstance().getTextureSlotFor(texture);
        }

        GLES20.glGenTextures(1, TextureManager.getInstance().getTextureHandlerArrayFromTextureInfo(textureInfo),
                textureInfo.getSlot());
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                TextureManager.getInstance().getTextureHandlerArrayFromTextureInfo(textureInfo)[textureInfo.getSlot()]);

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

        if (TextureManager.getInstance().getTextureHandlerArrayFromTextureInfo(textureInfo)[textureInfo.getSlot()] == 0)
            throw new RuntimeException("Error loading textureBitmap.");

        this.textureDataHandler = TextureManager.getInstance().getTextureHandlerArrayFromTextureInfo(textureInfo)[textureInfo.getSlot()];
        this.textureBloc = textureInfo.getBloc();
    }

    public Material(float[] color, float aLight,
                    float[] sLight, float shininess) {
        this.diffuseColor = color;
        this.ambientIntensity = aLight;
        this.specularColor = sLight;
        this.shininess = shininess;
    }

    public Material(float aLight, float[] dLight,
                    float[] sLight, float shininess) {
        this.ambientIntensity = aLight;
        this.diffuseColor = dLight;
        this.specularColor = sLight;
        this.shininess = shininess;
    }

    public float[] getColor() {
        return diffuseColor;
    }

    public float getAmbientIntensity() {
        return ambientIntensity;
    }

    public float[] getDiffuseColor() {
        return diffuseColor;
    }

    public float[] getSpecularColor() {
        return specularColor;
    }

    public float getShininess() {
        return shininess;
    }

    public boolean isTextured() {
        return textured;
    }

    public int getTextureDataHandler() {
        return textureDataHandler;
    }

    public int getTextureBloc() {
        return textureBloc;
    }

    public void setColor(float[] color) {
        this.diffuseColor = color;
    }

    public void dispose() {
        if (textureBitmap != null)
            textureBitmap.recycle();

        if (isTextured())
            TextureManager.getInstance().cleanupTexture(textureInfo);
    }

    public void setTexture(Bitmap bitmap) {
        loadTexture(bitmap);
    }

    public void setTexture(String textureFile) {
        loadTexture(PacManGLRenderer.loadImage(textureFile));
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }


    public void setAmbientIntensity(float ambientIntensity) {
        this.ambientIntensity = ambientIntensity;
    }

    public void setDiffuseColor(float[] diffuseLight) {
        this.diffuseColor = diffuseLight;
    }

    public void setSpecularColor(float[] specularLight) {
        this.specularColor = specularLight;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
}
