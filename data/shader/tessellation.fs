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

#version 400
out vec4 FragColor;
in vec2 gTexCoord;

uniform sampler2D jvr_Texture1;
uniform sampler2D jvr_Texture2;

void main()
{
	vec3 normal = texture2D(jvr_Texture2, gTexCoord).rgb * 2.0 - 1.;
	float intensity = max(dot(normalize(vec3(-1, 1, 0.75)), normalize(normal)), 0.0);
	FragColor.rgb = 1.5 * intensity * texture2D(jvr_Texture1, gTexCoord).rgb;
	FragColor.a = 1;
}