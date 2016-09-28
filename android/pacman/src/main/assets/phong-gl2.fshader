precision mediump float;

uniform vec3 uLight, uColor;
uniform vec3 uAmbient, uDiffuse, uSpecular;
uniform float uMaterial;
uniform float uAttConst, uAttLin, uAttExp;

varying vec3 vNormal;
varying vec3 vPosition;

void main() {
    vec3 lightDir = normalize(uLight - vPosition);
    vec3 eye = normalize(-vPosition); //check if eye and view coordinates match
    vec3 reflectance = normalize(reflect(lightDir, vNormal));

    vec4 diffuse = vec4(0.0, 0.0, 0.0, 0.0);
    vec4 specular = vec4(0.0, 0.0, 0.0, 0.0);

    //ambient
    vec4 ambient = vec4(uAmbient, 0.0);

    //diffuse
    diffuse = vec4(uDiffuse * max(dot(vNormal,lightDir), 0.0), 0.0);
    diffuse = clamp(diffuse, 0.0, 1.0);

    //specular
    specular = vec4(uSpecular*pow(max(dot(reflectance,eye),0.0),uMaterial),0.0);
    specular = clamp(specular, 0.0, 1.0);

    //output color
    float dist = distance(uLight, vPosition);
    float attenuation = uAttConst + uAttLin * dist + uAttExp * pow(dist, 2);
    gl_FragColor = vec4(uColor, 0.0).rgba * (ambient + diffuse + specular)/attenuation;
}
