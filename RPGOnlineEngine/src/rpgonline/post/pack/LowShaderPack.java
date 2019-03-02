package rpgonline.post.pack;

import rpgonline.post.DynamicHeatShader2;
import rpgonline.post.MultiEffect;

/**
 * A low quality shader pack for low end devices.
 * 
 * @author Tomas
 */
public class LowShaderPack extends MultiEffect {
	/**
	 * Create the shader pack.
	 */
	public LowShaderPack() {
		super(new DynamicHeatShader2("get heat"));
	}
}
