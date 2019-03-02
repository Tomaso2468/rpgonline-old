package rpgonline.world;

import org.newdawn.slick.Color;

public interface LightSource {
	public double getLX();
	public double getLY();
	public default float getR() {
		return getColor().r * getBrightness();
	}
	public default float getG() {
		return getColor().g * getBrightness();
	}
	public default float getB() {
		return getColor().b * getBrightness();
	}
	public Color getColor();
	public default float getBrightness() {
		return 1;
	}
}
