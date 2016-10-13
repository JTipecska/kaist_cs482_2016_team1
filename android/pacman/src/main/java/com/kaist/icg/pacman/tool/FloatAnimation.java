package com.kaist.icg.pacman.tool;

import android.os.SystemClock;

/**
 * Generate a linear interpolation between 2 float on a given time
 * with loop and reverse functionality
 */
public class FloatAnimation {
    private float from;
    private float to;
    private long startTime;
    private long animationTime;
    private float percent;
    private boolean loop;
    private boolean reverse;
    private boolean isReversing;
    private long overflow;
    private float current;
    private boolean end;

    public FloatAnimation(float from, float to, long animationTime) {
        this.from = from;
        this.to = to;
        this.animationTime = animationTime;

        startTime = SystemClock.uptimeMillis();
        current = from;
    }

    public FloatAnimation(float from, float to, long animationTime, boolean loop, boolean reverse) {
        this.from = from;
        this.to = to;
        this.animationTime = animationTime;
        this.loop = loop;
        this.reverse = reverse;

        startTime = SystemClock.uptimeMillis();
        current = from;
    }

    public float update() {
        if(end) return current;

        percent = ((float)SystemClock.uptimeMillis() - startTime) / animationTime;

        if(percent >= 1) {
            if(!loop && !reverse) {
                end = true;
                current = to;
                return to;
            }
            else if(loop && !reverse) {}
            else if(loop && reverse && isReversing)
                isReversing = false;
            else if(!isReversing)
                isReversing = true;
            else {
                end = true;
                current = from;
                return from;
            }

            overflow = SystemClock.uptimeMillis() - startTime - animationTime;
            startTime = SystemClock.uptimeMillis() - overflow;
            percent = overflow / animationTime;
        }


        if(!isReversing)
            current = from + (to - from) * percent;
        else
            current = to - (to - from) * percent;

        return current;
    }

    public float getValue() {
        return current;
    }
}