package rpgonline.post.pack;

import rpgonline.post.ColorBoostEffect;
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
		super(new MotionBlur(0.35f), new DynamicHeatShader2("get heat"), new EdgeResample(), new FragmentExpose(),
				new ColorBoostEffect());
	}
}