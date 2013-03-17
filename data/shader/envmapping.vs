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

uniform mat4 jvr_ModelViewMatrix;
uniform mat4 jvr_ModelViewProjectionMatrix;
uniform mat3 jvr_NormalMatrix;

varying vec3 ReflectDir;

void main(void)
{
	gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
	
	vec3 eyeVec = (jvr_ModelViewMatrix * jvr_Vertex).xyz;
	vec3 normal = normalize(jvr_NormalMatrix * jvr_Normal);
	ReflectDir  = reflect(eyeVec, normal);	
}