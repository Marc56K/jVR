varying vec2 texCoord;

void main (void)
{
  vec2 check = vec2(floor(texCoord * 50.0));
  if (mod((check.x + check.y), 2.0) > 0.0)
  	gl_FragColor = vec4(0.3, 0.3, 0.3, 1);
  else
    gl_FragColor = vec4(0.7, 0.7, 0.7, 1);	
}
