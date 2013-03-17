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

uniform vec3 toonColor;
uniform vec3 lightIntensity;

uniform vec3 ka, kd, ks;
uniform float ke;

varying vec3 normalV;
varying vec3 lightDirV;
varying vec3 eyeDirV;

void main (void)
{
	vec3 N = normalize(normalV);
	vec3 L = normalize(lightDirV);
	vec3 E = normalize(eyeDirV);
	
	/* diffuse intensity */
	float intensity = dot(L, N);

	vec3 color = ka * lightIntensity;
	if (intensity > 0.0) {
		/* diffuse reflectance */
		color += kd * lightIntensity * intensity;
		/* specular highlight */
		vec3 R = reflect(-L, N);
		color += ks * lightIntensity * pow(max(dot(R, E), 0.0), ke );
	}
	gl_FragColor	=	vec4(color ,	1);	
}
