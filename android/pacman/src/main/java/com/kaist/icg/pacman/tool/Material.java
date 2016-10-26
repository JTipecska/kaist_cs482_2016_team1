package com.kaist.icg.pacman.tool;

/**
 * Created by Lou on 15.10.2016.
 */

// TODO: define materials like 'Ghost material' and pass it to the drawables.
public class Material {
    private float[] color = {0, 0, 0};
    //TODO: we might just set a costant ambientLight for the whole scene
    private float[] ambientLight = {0.1f, 0.1f, 0.1f};
    private float[] diffuseLight = {0.1f, 0.1f, 0.1f};
    private float[] specularLight = {0.1f, 0.1f, 0.1f};
    private float shininess = 0.1f;

    public Material(float[] color){
        this.color = color;
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

    public void setColor(float[] color) {this.color = color;}
}
