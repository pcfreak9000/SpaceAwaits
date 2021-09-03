attribute vec2 pos;

uniform vec4 corners;

varying vec2 v_pos;

void main(){
	vec2 c = 0.5*pos+0.5;
	
	v_pos = vec2(mix(corners.x, corners.z, c.x), mix(corners.y, corners.w, c.y));
	
    gl_Position = vec4(pos, 0.0, 1.0);
}