package com.kaist.icg.pacman.graphic.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.nio.FloatBuffer;

public class TextElement extends UIElement {
    private String text;
    private Bitmap bitmap;
    private Paint paint;
    private Rect bounds;
    private int backgroundColor;
    private int foregroundColor;
    private boolean isDirty;

    public TextElement(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer) {
        super(vertexBufferSize, vertexBuffer, normalBuffer, textureCoordinatesBuffer);

        this.foregroundColor = Color.WHITE;
        this.backgroundColor = Color.BLUE;
        this.isDirty = true;

        paint = new Paint();
        paint.setTextSize(32);
        paint.setAntiAlias(true);
        paint.setColor(foregroundColor);

        bounds = new Rect();
    }

    @Override
    public void draw() {
        if(this.isDirty)
            renderTexture();
        super.draw();
    }

    public void setText(String text) {
        this.text = text;
        this.isDirty = true;
    }

    public void setTextSize(float size) {
        paint.setTextSize(size);
        this.isDirty = true;
    }

    public void setBackgroundColor(int backgroundColor) {
        if(this.backgroundColor != backgroundColor)
            this.isDirty = true;
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        if(this.foregroundColor != foregroundColor)
            this.isDirty = true;
        this.foregroundColor = foregroundColor;
    }

    private void renderTexture() {
        if(bitmap != null)
            bitmap.recycle();

        paint.getTextBounds(text, 0, text.length(), bounds);
        bounds.right += 5;

        bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(backgroundColor);

        canvas.drawText(text, 0, bounds.height(), paint);

        setTexture(bitmap);
        setSize(bounds.width(), bounds.height());
        this.isDirty = false;
    }

    public Rect getBounds() {
        if(this.isDirty)
            renderTexture();
        return bounds;
    }
}
