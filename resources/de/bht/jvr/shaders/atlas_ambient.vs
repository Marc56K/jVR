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
attribute vec4 jvr_TexCoordTrans;

uniform mat4 jvr_ModelViewProjectionMatrix;
uniform mat4 jvr_ProjectionMatrix;
uniform mat4 jvr_ModelViewMatrix;
uniform float jvr_PolygonOffset;

varying vec2 texCoord;
varying vec4 texCoordTrans;

void jvr_calculateClipping(vec4 vertex);

void main(void)
{
	texCoord = jvr_TexCoord;
	texCoordTrans = jvr_TexCoordTrans;
	
	if(jvr_PolygonOffset == 0.0)
	{
		gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
	}
	else
	{
		gl_Position = jvr_ModelViewMatrix * jvr_Vertex;
		vec3 eyeVec = normalize(gl_Position.xyz);
		gl_Position.xyz += eyeVec*jvr_PolygonOffset;// the shadow map bias
		gl_Position = jvr_ProjectionMatrix * gl_Position;
	}
	
	// user defined clipping
	jvr_calculateClipping(jvr_ModelViewMatrix * jvr_Vertex);
}