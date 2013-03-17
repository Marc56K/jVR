attribute vec4 jvr_Vertex;
attribute vec2 jvr_TexCoord;
attribute vec3 jvr_Tangent;

uniform mat4  jvr_ProjectionMatrix;
uniform mat4  jvr_ModelViewMatrix;

varying vec2 texCoord;

void main(void)
{
	gl_Position = jvr_ProjectionMatrix * jvr_ModelViewMatrix * jvr_Vertex;
  texCoord = jvr_TexCoord;
}