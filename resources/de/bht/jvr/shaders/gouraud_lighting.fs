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
uniform bool  jvr_LightSource_CastShadow;

uniform sampler2D jvr_Texture0;
uniform bool      jvr_UseTexture0;
uniform vec4      jvr_Material_Specular;
uniform vec4      jvr_Material_Diffuse;
uniform float     jvr_Material_Shininess;

varying vec3  normal;
varying vec3  eyeVec;
varying vec3  lightDir;
varying vec2  texCoord;

uniform sampler2D jvr_ShadowMap;
varying vec4 shadowCoord;

varying vec4 color;

void jvr_discardIfClipped();

float lookup(float x, float y)
{
	vec4 sc = shadowCoord / shadowCoord.w;
	sc.x+=x;
	sc.y+=y;
	float distanceFromLight = texture2D(jvr_ShadowMap, sc.xy).z;
	
	return distanceFromLight < sc.z ? 0.0: 1.0; 	
}

void main (void)
{
    jvr_discardIfClipped();

   	vec4 color = color;
   	if (jvr_UseTexture0)
   	{
        color *= texture2D(jvr_Texture0, texCoord);        
    }
    if(color.w < 0.2) discard;
    
    vec3 L = normalize(lightDir);	
	
	if (jvr_LightSource_SpotCutOff == 0.0)
	{	
		// point light or directional light
		gl_FragColor = color;
	}
	else
	{
		vec3 D = normalize(jvr_LightSource_SpotDirection);
		float LdotD = dot(-L, D);
		if(LdotD > jvr_LightSource_SpotCosCutOff)
		{
			// spot light			
			if(jvr_LightSource_CastShadow)
			{
				// with shadow mapping
				int samples = 2;
				float sum = 0.0;
				int x;
				int y;
				for(x = -samples; x<samples; x++)
				{
					for(y = -samples; y<samples; y++)
					{
						sum += lookup(float(x)*0.0002, float(y)*0.0002);
					}
				}
				
				sum /= float((2*samples)*(2*samples));
				
				if(sum > 0.0)
				{
					gl_FragColor = color;
					gl_FragColor.w = sum;
				}
				else
				{
					discard;
				}
			}
			else
			{
				// without shadow mapping
				gl_FragColor = color;	
			}
		}
		else
		{
			discard;
		}
	}
}