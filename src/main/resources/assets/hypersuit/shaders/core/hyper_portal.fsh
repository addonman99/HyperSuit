#version 150

uniform float GameTime;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord0;

    float flowA = sin((uv.x * 17.0 + uv.y * 11.0) - GameTime * 8.0);
    float flowB = sin((uv.y * 23.0 - uv.x * 7.0) + GameTime * 5.0);
    float veins = sin((uv.x + uv.y) * 39.0 + flowA * 2.0 + GameTime * 3.0);
    float energy = clamp(0.48 + flowA * 0.18 + flowB * 0.16 + veins * 0.12, 0.08, 1.0);

    vec3 deepRed = vec3(0.20, 0.002, 0.012);
    vec3 hotRed  = vec3(1.00, 0.025, 0.055);
    vec3 color = mix(deepRed, hotRed, energy);

    fragColor = vec4(color * vertexColor.rgb, 0.78 * vertexColor.a);
}
