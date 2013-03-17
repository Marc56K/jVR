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

uniform mat4 jvr_ProjectionMatrix;
uniform sampler2D jvr_MirrorTexture;

uniform float waveTime;
uniform float waveScale;

varying vec3 eyeDir;
varying vec4 vertex;
varying vec2 texCoord;

void main (void)
{	
	// Moving from [-1,1] to [0,1]
	mat4 DCtoTC = mat4(	0.5, 0.0, 0.0, 0.0,
						0.0, 0.5, 0.0, 0.0,
						0.0, 0.0, 0.5, 0.0,
						0.5, 0.5, 0.5, 1.0);

  
    // Project an homogenize vertex to yield texture coordinates.
	vec4 vertexNDC = DCtoTC * jvr_ProjectionMatrix * vertex;	
	vec2 texCoord = vertexNDC.xy / vertexNDC.w;

	texCoord.x = 1.0-texCoord.x;
	
	// Make a cyanish tint.
	gl_FragColor = texture2D(jvr_MirrorTexture, texCoord);
}