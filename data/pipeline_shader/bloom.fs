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

uniform sampler2D jvr_Texture0;
uniform sampler2D jvr_Texture1;
varying vec2 texCoord;

vec4 fastblur(float intensity)
{    	
   	// use mipmapping levels
	vec4 final_color = texture2DLod(jvr_Texture0, texCoord, intensity);
   	final_color.w = 1.0;
	return final_color;
}

void main (void)
{
	gl_FragColor = fastblur(5.5) + texture2D(jvr_Texture1, texCoord);
}