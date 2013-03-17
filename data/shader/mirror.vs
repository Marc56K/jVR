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

uniform mat4 jvr_ModelViewProjectionMatrix;
uniform mat4 jvr_ModelViewMatrix;
uniform mat3 jvr_NormalMatrix;

varying vec3 eyeDir;
varying vec4 vertex;
varying vec2 texCoord;

void main(void)
{
	vertex = jvr_ModelViewMatrix * jvr_Vertex;
	
  // In camera coordinates.
  eyeDir = -vertex.xyz;
  
  vec3 n = normalize(jvr_NormalMatrix * jvr_Normal);
  vec3 t = normalize(jvr_NormalMatrix * jvr_Tangent);
  vec3 b = normalize(jvr_NormalMatrix * jvr_Binormal);
  
  mat3 tbn = mat3(t.x, b.x, n.x,
                  t.y, b.y, n.y,
                  t.z, b.z, n.z);
  
  // Transform to tangent space.
  eyeDir = tbn * eyeDir;

  texCoord = jvr_TexCoord;
  gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
}