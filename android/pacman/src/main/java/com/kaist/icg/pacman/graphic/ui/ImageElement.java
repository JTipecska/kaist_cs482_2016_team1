package com.kaist.icg.pacman.graphic.ui;

import java.nio.FloatBuffer;

public class ImageElement extends UIElement {
    public ImageElement(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer) {
        super(vertexBufferSize, vertexBuffer, normalBuffer, textureCoordinatesBuffer);
    }
}
