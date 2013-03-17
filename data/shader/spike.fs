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

uniform vec4  jvr_Material_Ambient;
uniform vec4  jvr_Material_Specular;
uniform vec4  jvr_Material_Diffuse;
uniform float jvr_Material_Shininess;
uniform vec4  jvr_LightSource_Diffuse;
uniform vec4  jvr_LightSource_Specular;

varying vec3 lightDirG;
varying vec3 eyeDirG;
varying vec3 normalG;

// simple phong model implementation
void main()
{
	// normalize
	vec3 light = normalize(lightDirG);
	vec3 normal = normalize(normalG);
	vec3 eye = normalize(eyeDirG);

	// ambient term
	vec4 ambient = jvr_Material_Ambient;
	vec4 diffuse = vec4(0, 0, 0, 1);
	vec4 specular = vec4(0, 0, 0, 1);
	// calculate lighting for backfacing polygons also
	if (dot(normal, eye) < 0.0)
		normal = normal * -1.0;
	
	// only if oriented towards the light
	float nl = dot(normal, light);
	if(nl >= 0.0) {
		// diffuse term
		diffuse = jvr_Material_Diffuse * jvr_LightSource_Diffuse * nl;
		// only if reflection oriented towards eye
		float re = dot(reflect(light, normal), -eye);
		if ( re >= 0.0) {
			// specular term
			specular = jvr_Material_Specular * jvr_LightSource_Specular * pow(max(re, 0.0), jvr_Material_Shininess);
		}
	}
	// combine
	gl_FragColor = ambient + diffuse + specular ;
}
		