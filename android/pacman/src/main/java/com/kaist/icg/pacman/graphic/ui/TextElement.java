package com.kaist.icg.pacman.graphic.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.kaist.icg.pacman.graphic.android.PacManActivity;
import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;

import java.nio.FloatBuffer;

public class TextElement extends UIElement {
    private static Paint backgroundImagePaint;
    private static Typeface textTypeface;

    private String text;
    private Bitmap bitmap;
    private Paint paint;
    private int backgroundColor;
    private int foregroundColor;
    private Bitmap backgroundImage;
    private boolean isDirty;
    private Rect textBounds;
    private Rect padding;

    public TextElement(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer) {
        super(vertexBufferSize, vertexBuffer, normalBuffer, textureCoordinatesBuffer);

        if(backgroundImagePaint == null) {
            backgroundImagePaint = new Paint();
            backgroundImagePaint.setAntiAlias(true);
            backgroundImagePaint.setColor(Color.WHITE);

            textTypeface = Typeface.createFromAsset(PacManActivity.context.getAssets(), "font.ttf");
        }

        this.foregroundColor = Color.WHITE;
        this.backgroundColor = Color.BLUE;
        this.isDirty = true;

        paint = new Paint();
        paint.setTextSize(32);
        paint.setAntiAlias(true);
        paint.setColor(foregroundColor);
        paint.setTypeface(textTypeface);

        textBounds = new Rect();
        padding = new Rect();
        text = "";
    }

    @Override
    public void draw() {
        if(this.isDirty)
            renderTexture();
        super.draw();
    }

    public void setText(String text) {
        if(!this.text.equals(text)) {
            this.text = text;
            this.isDirty = true;
        }
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
        this.paint.setColor(foregroundColor);
    }

    public void setBackgroundImage(String backgroundImageFile) {
        if(this.backgroundImage != null)
            this.backgroundImage.recycle();
        this.backgroundImage = PacManGLRenderer.loadImage(backgroundImageFile);
        this.isDirty = true;
    }

    private void renderTexture() {
        if(bitmap != null)
            bitmap.recycle();

        paint.getTextBounds(text, 0, text.length(), textBounds);
        textBounds.right += 5;

        bitmap = Bitmap.createBitmap(textBounds.width() + padding.left + padding.right,
                textBounds.height() + padding.top + padding.bottom,
                Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(backgroundColor);

        if(this.backgroundImage != null)
            canvas.drawBitmap(this.backgroundImage,
                    new Rect(0, 0, this.backgroundImage.getWidth(), this.backgroundImage.getHeight()),
                    new Rect(0, 0, textBounds.width() + padding.left + padding.right,
                            textBounds.height() + padding.top + padding.bottom),
                    backgroundImagePaint);

        canvas.drawText(text, padding.left, textBounds.height() + padding.top, paint);

        this.updateBounds();
        setTexture(bitmap);
        setScreenSize(textBounds.width() + padding.left + padding.right,
                textBounds.height() + padding.top + padding.bottom);
        this.isDirty = false;
    }

    public void setPadding(int top, int right, int bottom, int left) {
        padding.set(left, top, right ,bottom);
        this.isDirty = true;
    }

    public void dispose() {
        super.dispose();

        if(bitmap != null)
            bitmap.recycle();

        if(backgroundImage != null)
            backgroundImage.recycle();
    }
}
