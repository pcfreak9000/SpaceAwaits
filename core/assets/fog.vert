attribute vec2 pos;

uniform mat4 u_projView;

varying vec2 v_pos;

void main(){
	
	v_pos = pos;
	
    gl_Position = u_projView*vec4(pos, 0.0, 1.0);
}