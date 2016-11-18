precision mediump float;

uniform vec3 uLight, uColor, uSpecular;
uniform float uAmbient, uShininess;
uniform sampler2D uNormal;

varying vec3 vNormal;
varying vec3 vPosition;
varying vec2 vTextureCoordinate;

const float uAttConst = 1.0, uAttLin = 0.05, uAttExp = 0.1;

void main()
{
 vec3 L = normalize( uLight - vPosition);
 vec3 V = normalize(-vPosition);
 vec3 normal = texture2D(uNormal, vTextureCoordinate).rgb;

 float diffuse = max(0.0, dot(L,normal));

 vec3 H = normalize(L + V);

 float specular = 0.0;

 if( dot(L,normal) > 0.0)
 {
 // can be multiplied by material constant
   specular = pow( max(0.0, dot( H, normal)), uShininess);
 }

 //limit specular
 float specMask = (pow(dot(H, normal), uShininess) > 0.4) ? 1.0 : 0.0;

float dist = distance(uLight, vPosition);
float attenuation = uAttConst + uAttLin * dist + uAttExp * pow(dist, 2.0);

 vec4 color = vec4(uColor, 1.0) * diffuse/attenuation;//
 gl_FragColor = color;
}
