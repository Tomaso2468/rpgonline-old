package rpgonline.world;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.math3.util.FastMath;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import rpgonline.TextureMap;
import rpgonline.net.ServerManager;
import rpgonline.tile.Tiles;

/**
 * An interface for tile independent game objects.
 * @author Tomas
 */
public interface Entity extends Serializable {
	/**
	 * A reusable array for lighting.
	 */
	public static final float[] colors = new float[3];

	/**
	 * Gets the {@code x} position of this tile.
	 * @return A double value.
	 */
	public double getX();

	/**
	 * Gets the {@code y} position of this tile.
	 * @return A double value.
	 */
	public double getY();

	/**
	 * Renders an entity texture.
	 * @param texture A texture ID.
	 * @param container A container holding the game.
	 * @param game The game.
	 * @param g The screen's graphics.
	 * @param x The {@code x} position of this entity.
	 * @param y The {@code y} position of this entity.
	 * @param z The {@code z} position of this entity.
	 * @param world The game world.
	 * @param scale The value to scale the texture by.
	 * @param lights A list of nearby lights.
	 * @param sx The onscreen {@code x} position of the entity measured in tiles.
	 * @param sy The onscreen {@code y} position of the entity measured in tiles.
	 */
	public default void renderTexture(String texture, GameContainer container, StateBasedGame game, Graphics g,
			double x, double y, double z, World world, float scale, Collection<LightSource> lights, float sx, float sy) {
		if (texture == null) {
			return;
		}
		if(TextureMap.getTexture(texture) == null) {
			Log.warn("Invalid texture: " + texture);
		}
		Image img = TextureMap.getTexture(texture).getScaledCopy(scale);

		float[] colors = getLighting(world, lights, x + img.getWidth() / 64f, y + img.getHeight() / 64f);
		img.setImageColor(colors[0], colors[1], colors[2]);

		g.drawImage(img, sx * Tiles.getTileWidth(), sy * Tiles.getTileHeight());
	}

	public default void render(GameContainer container, StateBasedGame game, Graphics g, double x, double y, double z,
			World world, Collection<LightSource> lights, float sx, float sy) throws SlickException {
		renderTexture(getTexture(x, y, z, world), container, game, g, x, y, z, world, 1, lights, sx, sy);
	}

	public String getTexture(double x, double y, double z, World world);

	public static float[] getLighting(World world, Collection<LightSource> lights, double x, double y) {
		float red = world.getLightColor().r;
		float green = world.getLightColor().g;
		float blue = world.getLightColor().b;

		for (LightSource l : lights) {
			double dist = FastMath.hypot(x - l.getLX(), y - l.getLY());

			if (dist < 1) {
				dist = 1;
			}

			red += l.getR() / dist / FastMath.sqrt(dist);
			green += l.getG() / dist / dist;
			blue += l.getB() / dist / dist / dist;
		}

		colors[0] = red;
		colors[1] = green;
		colors[2] = blue;

		return colors;
	}

	public static float[] getLightingWithSun(World world, Collection<LightSource> lights, double x, double y) {
		float red = ServerManager.getServer().getSkyColor().r / 2;
		float green = ServerManager.getServer().getSkyColor().g / 2;
		float blue = ServerManager.getServer().getSkyColor().b / 2;

		red += ServerManager.getServer().getSunColor().r / 2;
		green += ServerManager.getServer().getSunColor().g / 2;
		blue += ServerManager.getServer().getSunColor().b / 2;

		for (LightSource l : lights) {
			double dist = FastMath.hypot(x - l.getLX(), y - l.getLY());

			if (dist < 1) {
				dist = 1;
			}

			red += l.getR() / dist / FastMath.sqrt(dist);
			green += l.getG() / dist / dist;
			blue += l.getB() / dist / dist / dist;
		}

		double sx = ServerManager.getServer().getX() + ServerManager.getServer().getSunX();
		double sy = ServerManager.getServer().getY() + FastMath.abs(ServerManager.getServer().getSunX()) / 3;
		double sdist = FastMath.hypot(sx - x, sy - y);

		red += 1.1 / sdist / sdist;
		green += 1 / sdist / FastMath.sqrt(sdist);
		blue += 1.5 / sdist;

		colors[0] = red;
		colors[1] = green;
		colors[2] = blue;

		return colors;
	}
	
	public default boolean isFlying() {
		return false;
	}
}
