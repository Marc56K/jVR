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

uniform mat4  jvr_ModelViewMatrix;
uniform mat4  jvr_ModelViewProjectionMatrix;
uniform mat3  jvr_NormalMatrix;

uniform vec4  jvr_LightSource_Diffuse;
uniform vec4  jvr_LightSource_Specular;
uniform vec4  jvr_LightSource_Position;
uniform float jvr_LightSource_ConstantAttenuation;
uniform float jvr_LightSource_LinearAttenuation;
uniform float jvr_LightSource_QuadraticAttenuation;
uniform mat4  jvr_LightSource_ModelViewProjectionMatrix;
uniform bool  jvr_LightSource_CastShadow;
uniform float jvr_LightSource_Intensity;
uniform float jvr_LightSource_SpotCutOff;
uniform float jvr_LightSource_SpotExponent;
uniform vec3  jvr_LightSource_SpotDirection;

uniform vec4  jvr_Material_Specular;
uniform vec4  jvr_Material_Diffuse;
uniform float jvr_Material_Shininess;

varying vec3  normal;
varying vec3  eyeVec;
varying vec3  lightDir;
varying vec2  texCoord;
varying vec4  shadowCoord;

varying vec4  color;

void jvr_calculateClipping(vec4 vertex);

vec4 phong(vec3 L, vec3 E, vec3 N, float attenuation)
{
	vec4 final_color = vec4(0,0,0,0);
	
	float lambertTerm = dot(N,L);
	if(lambertTerm > 0.0) 
	{
		final_color += attenuation * jvr_LightSource_Intensity * jvr_LightSource_Diffuse * jvr_Material_Diffuse * lambertTerm;
		//vec3 R = reflect(-L, N);
		//final_color += attenuation * jvr_LightSource_Intensity * jvr_LightSource_Specular * jvr_Material_Specular * pow( max(dot(R, E), 0.0), jvr_Material_Shininess);
	}
	
	return final_color;	
}

void main(void)
{	
	vec3 N = normalize(jvr_NormalMatrix * jvr_Normal);

	vec4 vVertex = jvr_ModelViewMatrix * jvr_Vertex;
	vec3 E = normalize(-vVertex.xyz);
	vec3 L;
	float attenuation = 1.0;
	
    if(jvr_LightSource_Position.w > 0.0)
    {
    	// point light or spot light

    	L = normalize( jvr_LightSource_Position.xyz - vVertex.xyz );
    	
    	float distance = length(lightDir);
    	attenuation = 1.0 / (jvr_LightSource_QuadraticAttenuation * distance * distance
                           + jvr_LightSource_LinearAttenuation * distance
                           + jvr_LightSource_ConstantAttenuation);
    }
    else
    {
    	// directional light

    	L = normalize( -jvr_LightSource_Position.xyz );
    	attenuation = 1.0;
    }

	texCoord = jvr_TexCoord;
	gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
	
	if(jvr_LightSource_CastShadow)
	{
		// Moving from [-1,1] to [0,1]
		mat4 DCtoTC = mat4(	0.5, 0.0, 0.0, 0.0,
							0.0, 0.5, 0.0, 0.0,
							0.0, 0.0, 0.5, 0.0,
							0.5, 0.5, 0.5, 1.0);
		shadowCoord = DCtoTC * jvr_LightSource_ModelViewProjectionMatrix * jvr_Vertex;
	}
	
	// lighting
	if (jvr_LightSource_SpotCutOff != 0.0)
	{	
		vec3 D = normalize(jvr_LightSource_SpotDirection);
		float LdotD = dot(-L, D);
		attenuation *= pow(LdotD, jvr_LightSource_SpotExponent);
	}
	
	color = phong(L, E, N, attenuation);
	
	lightDir = L;
	eyeVec = E;
	normal = N;
	
	// user defined clipping
	jvr_calculateClipping(jvr_ModelViewMatrix * jvr_Vertex);
}