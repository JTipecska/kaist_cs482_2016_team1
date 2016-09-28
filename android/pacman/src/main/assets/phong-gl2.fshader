precision mediump float;

uniform vec3 uLight, uColor;
uniform vec3 uAmbient, uDiffuse, uSpecular;
uniform float uMaterial;

varying vec3 vNormal;
varying vec3 vPosition;

void main() {
    vec3 light = normalize(uLight - vPosition);
    vec3 eye = normalize(-vPosition); //check if eye and view coordinates match
    vec3 reflectance = normalize(reflect(light, vNormal));

    vec4 diffuse = vec4(0.0, 0.0, 0.0, 0.0);
    vec4 specular = vec4(0.0, 0.0, 0.0, 0.0);

    //ambient
    vec4 ambient = vec4(uAmbient, 0.0);
    float dist = distance(uLight, vPosition);
    if (dist < 18.0f) {
        //diffuse
        diffuse = vec4(uDiffuse * max(dot(vNormal,light), 0.0), 0.0);
        diffuse = clamp(diffuse, 0.0, 1.0);

        //specular
        specular = vec4(uSpecular*pow(max(dot(reflectance,eye),0.0),uMaterial),0.0);
        specular = clamp(specular, 0.0, 1.0);
    }

    //output color
    gl_FragColor = vec4(uColor, 0.0).rgba * (ambient + diffuse + specular);
}
