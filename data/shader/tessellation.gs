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
layout(triangles) in;
layout(max_vertices = 3) out;
in vec2 teTexCoord[];
out vec2 gTexCoord;

uniform mat4 jvr_ModelViewProjectionMatrix;

void main()
{	
	gTexCoord = teTexCoord[0];
	gl_Position = jvr_ModelViewProjectionMatrix * gl_in[0].gl_Position; 
	EmitVertex();
	
	gTexCoord = teTexCoord[1];
	gl_Position = jvr_ModelViewProjectionMatrix * gl_in[1].gl_Position; 
	EmitVertex();
	
	gTexCoord = teTexCoord[2];
	gl_Position = jvr_ModelViewProjectionMatrix * gl_in[2].gl_Position; 
	EmitVertex();
	
	EndPrimitive();
}