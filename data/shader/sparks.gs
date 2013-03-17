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

float hs = 0.05;

varying in float partEnergyV[];
varying out float partEnergyG;

void quadVertex(float dx, float dy) {
	gl_Position = jvr_ProjectionMatrix * (gl_PositionIn[0] + vec4(dx, dy, 0, 0));
	partEnergyG = partEnergyV[0];
	EmitVertex();    
}

void main(void)
{
	if (partEnergyV[0] > 0.01) {
		quadVertex(-hs, -hs);
		quadVertex( hs, -hs);
		quadVertex(-hs,  hs);
		quadVertex( hs,  hs);
  		EndPrimitive();
	}
}