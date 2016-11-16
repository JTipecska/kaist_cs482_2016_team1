precision mediump float;

uniform vec3 uLight, uColor;
uniform vec3 uAmbient, uDiffuse, uSpecular;
uniform float uShininess;
uniform sampler2D uTexture;
uniform sampler2D uNormalMap;

varying vec3 vNormal;
varying vec3 vPosition;
varying vec2 vTextureCoordinate;
varying mat3 TBN;

//uniform float material_kd; //diffuse material constant
//uniform float material_ks; //specular material constant

//number of levels
//for diffuse color
const int levels = 5;
const float scaleFactor = 1.0 / float(levels);
const float uAttConst = 1.0, uAttLin = 0.05, uAttExp = 0.1;

void main()
{
 vec3 L = normalize( uLight - vPosition);
 vec3 V = normalize(-vPosition);

 vec3 L_tangentspace = TBN * L;
 vec3 V_tangentspace = TBN * V;
 vec3 N_tangentspace = normalize(
        texture2D(uNormalMap, vTextureCoordinate).rgb*2.0 - 1.0);

 float diffuse = max(0.0, dot(L,N_tangentspace));
 // can be multiplied by material constant
 diffuse = floor(diffuse * float(levels)) * scaleFactor;

 vec3 H = normalize(L_tangentspace + V_tangentspace);

 float specular = 0.0;

 if( dot(L_tangentspace,N_tangentspace) > 0.0)
 {
 // can be multiplied by material constant
   specular = pow( max(0.0, dot( H, N_tangentspace)), uShininess);
 }

 //limit specular
 float specMask = (pow(dot(H, vNormal), uShininess) > 0.4) ? 1.0 : 0.0;

float dist = distance(uLight, vPosition);
float attenuation = uAttConst + uAttLin * dist + uAttExp * pow(dist, 2.0);

 //not sure about the look of the specular component
 vec4 color = texture2D(uTexture, vTextureCoordinate);
 color = vec4(0.5, 0.3, 0.7);
 color = color * diffuse/attenuation + (vec4(0.4, 0.4, 0.4, 1.0)*specular * specMask)/attenuation;

color = vec4(N_tangentspace, 1.0);
 gl_FragColor = color;
}
