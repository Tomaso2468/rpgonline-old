package rpgonline.post.pack;

import rpgonline.post.ColorBoostEffect;
import rpgonline.post.DynamicHeatShader2;
import rpgonline.post.MultiEffect;

/**
 * A medium quality shader pack for average devices.
 * 
 * @author Tomas
 */
public class MediumShaderPack extends MultiEffect {
	/**
	 * Create the shader pack.
	 */
	public MediumShaderPack() {
		super(new DynamicHeatShader2("get heat"), new ColorBoostEffect());
	}
}
