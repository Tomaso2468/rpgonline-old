package rpgonline.post.pack;

import rpgonline.post.ColorEffectsShader;
import rpgonline.post.DynamicHeatShader2;
import rpgonline.post.EdgeResample;
import rpgonline.post.FragmentExpose;
import rpgonline.post.MotionBlur;
import rpgonline.post.MultiEffect;

/**
 * A high quality shader pack for high end devices.
 * 
 * @author Tomas
 */
public class ExtremeShaderPack extends MultiEffect {
	/**
	 * Create the shader pack.
	 */
	public ExtremeShaderPack() {
		//super(new DynamicHeatShader2("get heat"), new FragmentExpose(), new EdgeResample(), new ColorBoostEffect(), new MotionBlur(0.35f));
		super(new DynamicHeatShader2("get heat"), new FragmentExpose(), new EdgeResample(), new MotionBlur(0.35f), new ColorEffectsShader(1.05f /*s*/, 0f /*b*/, 1.3f /*c*/, 2.5f /*v*/, 0f /*h*/));
	}
}