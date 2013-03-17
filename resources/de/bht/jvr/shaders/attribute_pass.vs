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

attribute vec4 jvr_Vertex;
attribute vec2 jvr_TexCoord;
attribute vec3 jvr_Normal;
attribute vec3 jvr_Tangent;
attribute vec3 jvr_Binormal;

uniform mat4  jvr_ModelMatrix;
uniform mat4  jvr_ModelViewMatrix;
uniform mat4  jvr_ModelViewProjectionMatrix;
uniform mat3  jvr_NormalMatrix;

varying vec4 position;
varying vec4 worldPosition;
varying vec3 normal;
varying vec2 texCoord;
//varying vec3 tangent;
//varying vec3 binormal;

void jvr_calculateClipping(vec4 vertex);

void main(void)
{
	gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
	
	position = jvr_ModelViewMatrix * jvr_Vertex;
	worldPosition = jvr_ModelMatrix * jvr_Vertex;
	normal = jvr_NormalMatrix * jvr_Normal;
	texCoord = jvr_TexCoord;
	//tangent = jvr_NormalMatrix * jvr_Tangent;
	//binormal = jvr_NormalMatrix * jvr_Binormal;	
	
	// user defined clipping
	jvr_calculateClipping(jvr_ModelViewMatrix * jvr_Vertex);
}