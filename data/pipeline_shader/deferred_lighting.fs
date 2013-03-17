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

uniform vec4  jvr_LightSource_Diffuse;
uniform vec4  jvr_LightSource_Specular;
uniform float jvr_LightSource_Intensity;
uniform vec4  jvr_LightSource_Position;
uniform vec3  jvr_LightSource_SpotDirection;
uniform float jvr_LightSource_SpotExponent;
uniform float jvr_LightSource_SpotCutOff;
uniform float jvr_LightSource_SpotCosCutOff;

uniform sampler2D NormalTex;
uniform sampler2D EyeVecTex;
varying vec2  texCoord;

vec4 phong(vec3 L, vec3 E, vec3 N, vec4 kst, vec4 kdt)
{
	vec4 final_color = vec4(0,0,0,0);
	
	float lambertTerm = dot(N,L);
	if(lambertTerm > 0.0) 
	{
		final_color += jvr_LightSource_Intensity * jvr_LightSource_Diffuse * kdt * lambertTerm;
		vec3 R = reflect(-L, N);
		final_color += jvr_LightSource_Intensity * jvr_LightSource_Specular * kst * pow( max(dot(R, E), 0.0), 8.0);
	}
	
	return final_color;	
}

void main (void)
{   	
   	vec4 kst = vec4(0.9,0.9,0.9,1);
   	vec4 kdt = vec4(1,1,1,1);

	vec3 N = normalize(texture2D(NormalTex, texCoord).rgb);
	vec3 E = (texture2D(EyeVecTex, texCoord).rgb);
	vec3 L = normalize(jvr_LightSource_Position.xyz + E);
	E = normalize(E);

	gl_FragColor = phong(L, E, N, kst, kdt);
	gl_FragColor.a = 1.0;
}