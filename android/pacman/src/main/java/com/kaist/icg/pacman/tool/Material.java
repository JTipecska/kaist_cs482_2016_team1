package com.kaist.icg.pacman.tool;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;
import com.kaist.icg.pacman.manager.TextureManager;

// TODO: define materials like 'Ghost material' and pass it to the drawables.
public class Material {
    private float[] color = {1, 1, 1};
    //TODO: we might just set a costant ambientLight for the whole scene
    private float[] ambientLight = {0.1f, 0.0f, 0.0f};
    private float[] diffuseLight = {0.5f, 0.0f, 0.0f};
    private float[] specularLight = {1.0f, 1.0f, 1.0f};
    private float shininess = 16.0f;
    private float opacity = 1.0f;

    private boolean textured;
    private Bitmap textureBitmap;
    private int textureDataHandler;
    private int textureBloc;
    private TextureManager.TextureInfo textureInfo;

    private boolean normalMap = false;
    private Bitmap normalBitmap;
    private int normalDataHandler;
    private int normalBloc;
    private TextureManager.TextureSlot normalSlot;

    public Material(float[] color) {
        this.color = color;
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

    private void loadNormal(Bitmap normal) {
        this.normalBitmap = normal;
        this.normalMap = true;

        if (normalSlot == null)
            normalSlot = TextureManager.getInstance().getFreeTextureSlot();
        GLES20.glGenTextures(1, TextureManager.getInstance().getTexturePack(normalSlot.getBloc()), normalSlot.getSlot());
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                TextureManager.getInstance().getTexturePack(normalSlot.getBloc())[normalSlot.getSlot()]);

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

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, normalBitmap, 0);

        if (TextureManager.getInstance().getTexturePack(normalSlot.getBloc())[normalSlot.getSlot()] == 0)
            throw new RuntimeException("Error loading normalBitmap.");

        this.normalDataHandler = TextureManager.getInstance().getTexturePack(normalSlot.getBloc())[normalSlot.getSlot()];
        this.normalBloc = normalSlot.getBloc();
    }

    public Material(float[] color, float[] aLight, float[] dLight,
                    float[] sLight, float shininess) {
        this.color = color;
        this.ambientLight = aLight;
        this.diffuseLight = dLight;
        this.specularLight = sLight;
        this.shininess = shininess;
    }

    public Material(float[] aLight, float[] dLight,
                    float[] sLight, float shininess) {
        this.ambientLight = aLight;
        this.diffuseLight = dLight;
        this.specularLight = sLight;
        this.shininess = shininess;
    }

    public float[] getColor() {
        return color;
    }

    public float[] getAmbientLight() {
        return ambientLight;
    }

    public float[] getDiffuseLight() {
        return diffuseLight;
    }

    public float[] getSpecularLight() {
        return specularLight;
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
        this.color = color;
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

    public boolean hasNormalMap() {
        return normalMap;
    }

    public int getNormalDataHandler() {
        return normalDataHandler;
    }

    public int getNormalBloc() {
        return normalBloc;
    }

    public void disposeNormal() {
        if (normalBitmap != null)
            normalBitmap.recycle();

        if (hasNormalMap())
            TextureManager.getInstance().getTexturePack(normalSlot.getBloc())[normalSlot.getSlot()] = -1;
    }

    public void setNormalMap(Bitmap bitmap) {
        loadNormal(bitmap);
    }

    public void setNormalMap(String normalmapFile) {
        loadNormal(PacManGLRenderer.loadImage(normalmapFile));
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
}
