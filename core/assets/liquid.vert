attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec2 a_heightInfo;
attribute vec4 a_anim;

uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_pos;
varying vec2 v_sspos;
varying vec2 v_heightInfo;
varying vec4 v_anim;

void main(){
	v_color = a_color;
	v_color.a = v_color.a * (255.0/254.0);
	v_texCoords = a_texCoord0;
	v_pos = a_position.xy;
	vec4 blub = u_projTrans * a_position;
	v_sspos = blub.xy;
	v_heightInfo = a_heightInfo;
	v_anim = a_anim;
	gl_Position = blub;
}