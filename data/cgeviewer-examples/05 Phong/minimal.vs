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
attribute vec3 jvr_Normal;

uniform vec3 lightPosV;
uniform mat3 jvr_NormalMatrix;
uniform mat4 jvr_ModelViewMatrix;
uniform mat4 jvr_ModelViewProjectionMatrix;

varying vec3 normalV;
varying vec3 lightDirV;
varying vec3 eyeDirV;

void main(void)
{
	vec3 vertexV = (jvr_ModelViewMatrix * jvr_Vertex).xyz;
	eyeDirV = -vertexV;
	lightDirV = lightPosV - vertexV;
	normalV = normalize(jvr_NormalMatrix * jvr_Normal);

	gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
}