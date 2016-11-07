precision mediump float;

uniform vec3 uLight, uColor;
uniform vec3 uAmbient, uDiffuse, uSpecular;
uniform float uShininess;
uniform sampler2D uTexture;

varying vec3 vNormal;
varying vec3 vPosition;
varying vec2 vTextureCoordinate;

//uniform float material_kd; //diffuse material constant
//uniform float material_ks; //specular material constant

//number of levels
//for diffuse color
const int levels = 5;
const float scaleFactor = 1.0 / float(levels);
const float uAttConst = 2.5, uAttLin = 0.1, uAttExp = 0.2;

void main()
{
 vec3 L = normalize( uLight - vPosition);
 vec3 V = normalize(-vPosition);

 float diffuse = max(0.0, dot(L,vNormal));
 // can be multiplied by material constant
 diffuse = floor(diffuse * float(levels)) * scaleFactor;

 vec3 H = normalize(L + V);

 float specular = 0.0;

 if( dot(L,vNormal) > 0.0)
 {
 // can be multiplied by material constant
   specular = pow( max(0.0, dot( H, vNormal)), uShininess);
 }

 //limit specular
 float specMask = (pow(dot(H, vNormal), uShininess) > 0.4) ? 1.0 : 0.0;

float dist = distance(uLight, vPosition);
float attenuation = uAttConst + uAttLin * dist + uAttExp * pow(dist, 2.0);

 //not sure about the look of the specular component
 vec4 color = texture2D(uTexture, vTextureCoordinate);
 color = color * diffuse/attenuation;// + (vec4(0.4, 0.4, 0.4, 1.0)*specular * specMask)/attenuation;


 gl_FragColor = color;
}
