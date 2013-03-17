/**
 * Copyright 2013 Marc Roßbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#version 150
#extension GL_EXT_geometry_shader4 : enable

layout(triangles) in;
layout(max_vertices = 9) out;

// vertex program for the spikes demo
// input normals for the triangle
in vec3 normalV[];

// output vertex attributes
out vec3 lightDirG;
out vec3 eyeDirG;
out vec3 normalG;

uniform vec4  jvr_LightSource_Position;
uniform mat4  jvr_ProjectionMatrix;
uniform mat4  jvr_ModelViewProjectionMatrix;

uniform float offset;

// calculate eye direction for point
vec3 eyeDir(vec3 p) {
	return normalize(-p);
}
// calculate light direction for point
vec3 lightDir(vec3 p) {
	return normalize(jvr_LightSource_Position.xyz - p);
}

// project a point
vec4 project(vec3 p) {
	return jvr_ProjectionMatrix * vec4(p, 1.0);
}
// generate a single output vertex complete with all attributes
void vertex(vec3 p, vec3 n) {
	gl_Position = project(p);
	normalG = n;
	lightDirG = lightDir(p);
	eyeDirG = eyeDir(p);
	EmitVertex();
}
// generate a complete output triangle from three vertices
void triangle(vec3 p0, vec3 p1, vec3 p2) {
	// calculate correct normal
	vec3 n = normalize(cross(p1 - p0, p2 - p0));
	vertex(p0, n);
	vertex(p1, n);
	vertex(p2, n);
	EndPrimitive();
}

// testing : just pass the input triangle through to the output
void pass() {
	for(int i = 0; i != 3; i++) 
		vertex(gl_PositionIn[i].xyz, normalV[i]);
	EndPrimitive();
}

// replace the input triangle with a spike consisting of three triangles
void spike () {
	// the input vertices
	vec3 p0 = gl_PositionIn[0].xyz;
	vec3 p1 = gl_PositionIn[1].xyz;
	vec3 p2 = gl_PositionIn[2].xyz;
	// calculate the midpoint and offset it along the normal
	vec3 n = cross(p1 - p0, p2 - p0) * offset;
	vec3 p3 = (p0 + p1 + p2) / 3.0 + n;
	// generate three output triangles
	triangle( p0 , p1 , p3 );
	triangle( p1 , p2 , p3 );
	triangle( p2 , p0 , p3 );
}

void main()
{
	spike();
}
