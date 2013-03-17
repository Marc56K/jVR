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

float pi = 2.0 * asin(1.0);

// Hard coded waves on the completely flat mirror surface
vec3 waveNormal(float length, float amplitude,
                float speed, float exponent,
                vec2 direction, vec2 position)
{
  // See: http://http.developer.nvidia.com/GPUGems/gpugems_ch01.html Eq. 8b
  float frequency = 2.0 * pi / length;
  float angle =
    dot(direction, position) * frequency
    + waveTime * speed * frequency;
  float factor =
    exponent * frequency * amplitude * waveScale *
    pow((sin(angle) + 1.0) / 2.0,
        exponent - 1.0)
    * cos(angle);
  return normalize(vec3(-direction.x * factor,
                        -direction.y * factor,
                        1.0));
}

void main (void)
{	
	// Moving from [-1,1] to [0,1]
	mat4 DCtoTC = mat4(	0.5, 0.0, 0.0, 0.0,
						0.0, 0.5, 0.0, 0.0,
						0.0, 0.0, 0.5, 0.0,
						0.5, 0.5, 0.5, 1.0);

    // Wave parameters.
    float wL0 = 0.5;
    float wA0 = 0.2;
    float wS0 = 0.3;
    float wK0 = 2.5;
    vec2  wD0 = normalize(vec2(1, 1));

    // Wave parameters.
    float wL1 = 1.0;
    float wA1 = 0.5;
    float wS1 = 0.2;
    float wK1 = 1.0;
    vec2  wD1 = normalize(vec2(1, 0.8));

    // Wave parameters.
    float wL2 = 0.3;
    float wA2 = 0.1;
    float wS2 = 0.1;
    float wK2 = 2.5;
    vec2  wD2 = normalize(vec2(1, 1.4));

    // Wave parameters.
    float wL3 = 0.5;
    float wA3 = 0.2;
    float wS3 = 0.3;
    float wK3 = 2.5;
    vec2  wD3 = normalize(vec2(1, 0.9));
  
    vec3 wN0 = waveNormal(wL0, wA0, wS0, wK0, wD0, texCoord * 200.0);
    vec3 wN1 = waveNormal(wL1, wA1, wS1, wK1, wD1, texCoord * 200.0);
    vec3 wN2 = waveNormal(wL2, wA2, wS2, wK2, wD2, texCoord * 200.0);
    vec3 wN3 = waveNormal(wL3, wA3, wS3, wK3, wD3, texCoord * 200.0);

    vec3 normal = normalize(wN0 + wN1 + wN2 + wN3);
    // vec3 normal = normalize(wN0);

    // vec3 normal = vec3(0, 0, 1);
  
    // Simple distortion. See
    // http://www.gamedev.net/reference/articles/article2138.asp
    vec4 distorted = vec4(vertex.xy + 1.6 * normal.xy, vertex.zw);
  
  // Project an homogenize vertex to yield texture coordinates.
	vec4 vertexNDC = DCtoTC * jvr_ProjectionMatrix * distorted;	
	vec2 texCoord = vertexNDC.xy / vertexNDC.w;

  // Flip.
	texCoord.x = 1.0-texCoord.x;
	
	// Make a cyanish tint.
	gl_FragColor = texture2D(jvr_MirrorTexture, texCoord);
	gl_FragColor.r *=0.5;
	gl_FragColor.gb *=0.75;
	
	// Alpha depends on viewing angle.
	gl_FragColor.a = 1.0 - dot(normalize(eyeDir), normal)*0.75;
}