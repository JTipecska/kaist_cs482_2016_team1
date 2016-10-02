precision mediump float;

uniform vec3 uLight, uColor;
uniform vec3 uAmbient, uDiffuse, uSpecular;
uniform float uMaterial;
uniform float uAttConst, uAttLin, uAttExp;

varying vec3 vNormal;
varying vec3 vPosition;

//uniform float material_kd; //diffuse material constant
//uniform float material_ks; //specular material constant

//number of levels
//for diffuse color
const int levels = 5;
const float scaleFactor = 1.0 / float(levels);

void main()
{
 vec3 L = normalize( uLight - vPosition);
 vec3 V = normalize(-vPosition);

 float diffuse = max(0.0, dot(L,vNormal));
 // can be multiplied by material constant
 vec3 diffuseColor = uDiffuse * floor(diffuse * float(levels)) * scaleFactor;

 vec3 H = normalize(L + V);

 float specular = 0.0;

 if( dot(L,vNormal) > 0.0)
 {
 // can be multiplied by material constant
   specular = pow( max(0.0, dot( H, vNormal)), uMaterial);
 }

 //limit specular
 float specMask = (pow(dot(H, vNormal), uMaterial) > 0.4) ? 1.0 : 0.0;

 //not sure about the look of the specular component
 vec3 color = (uColor + diffuseColor);// + specular * specMask);

float dist = distance(uLight, vPosition);
    float attenuation = uAttConst + uAttLin * dist + uAttExp * pow(dist, 2.0);

 gl_FragColor = vec4(color,1)/attenuation;
}
