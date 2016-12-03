precision mediump float;

uniform vec3 uLight, uColor, uSpecular;
uniform float uAmbient, uShininess;

varying vec3 vNormal;
varying vec3 vPosition;

const float uAttConst = 1.0, uAttLin = 0.05, uAttExp = 0.05;

void main() {
     vec3 L = normalize( uLight - vPosition);
     vec3 V = normalize(-vPosition);

     float diffuse = max(0.0, dot(L,vNormal));
     // can be multiplied by material constant

     vec3 H = normalize(L + V);

     float specular = 0.0;

     if( dot(L,vNormal) > 0.0)
     {
       specular = pow( max(0.0, dot( H, vNormal)), uShininess);
     }

    float dist = distance(uLight, vPosition);
    float attenuation = uAttConst + uAttLin * dist + uAttExp * pow(dist, 2.0);

     vec3 color = uAmbient * diffuse * uColor/attenuation + specular * uSpecular/attenuation;

     gl_FragColor = vec4(color,1);
}
