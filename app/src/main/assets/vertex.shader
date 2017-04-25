attribute vec3 vPos;
attribute in vec3 vCol;
uniform mat4 projectionMatrix;
uniform float z;
out vec3 colour;

void main() {
	colour = vCol;
	gl_Position = projectionMatrix * vec4(vPos, 1.0);
}
