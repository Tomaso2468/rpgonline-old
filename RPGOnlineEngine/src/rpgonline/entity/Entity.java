package rpgonline.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.util.FastMath;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import rpgonline.TextureMap;
import rpgonline.net.ServerManager;
import rpgonline.tile.Tiles;
import rpgonline.world.LightSource;
import rpgonline.world.World;

import static rpgonline.entity.UpdatePacket.*;

/**
 * A class for tile independent game objects.
 * 
 * @author Tomas
 */
public abstract class Entity implements Serializable {
	private static final long serialVersionUID = 2925012790649985351L;

	private static volatile long nextID = -1;

	protected long getNextID() {
		nextID += 1;
		return nextID;
	}

	private final Map<String, Object> objects = new HashMap<String, Object>();
	private final Map<String, Integer> ints = new HashMap<String, Integer>();
	private final Map<String, Long> longs = new HashMap<String, Long>();
	private final Map<String, Float> floats = new HashMap<String, Float>();
	private final Map<String, Double> doubles = new HashMap<String, Double>();
	private final Map<String, String> strings = new HashMap<String, String>();
	private final Map<String, Boolean> bools = new HashMap<String, Boolean>();
	private long id;

	public Entity() {
		this.id = getNextID();
	}

	/**
	 * A reusable array for lighting.
	 */
	public static final float[] colors = new float[3];

	/**
	 * Gets the {@code x} position of this tile.
	 * 
	 * @return A double value.
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 * Gets the {@code y} position of this tile.
	 * 
	 * @return A double value.
	 */
	public double getY() {
		return getDouble("y");
	}

	public void setX(double x) {
		setDouble("x", x);
	}

	public void setY(double y) {
		setDouble("y", y);
	}

	/**
	 * Renders an entity texture.
	 * 
	 * @param texture   A texture ID.
	 * @param container A container holding the game.
	 * @param game      The game.
	 * @param g         The screen's graphics.
	 * @param x         The {@code x} position of this entity.
	 * @param y         The {@code y} position of this entity.
	 * @param z         The {@code z} position of this entity.
	 * @param world     The game world.
	 * @param scale     The value to scale the texture by.
	 * @param lights    A list of nearby lights.
	 * @param sx        The onscreen {@code x} position of the entity measured in
	 *                  tiles.
	 * @param sy        The onscreen {@code y} position of the entity measured in
	 *                  tiles.
	 */
	public void renderTexture(String texture, GameContainer container, StateBasedGame game, Graphics g, double x,
			double y, double z, World world, float scale, Collection<LightSource> lights, float sx, float sy) {
		if (texture == null) {
			return;
		}
		Image img = TextureMap.getTexture(texture);
		if (img == null) {
			Log.warn("Invalid texture: " + texture);
		}
		img = img.getScaledCopy(scale);

		g.drawImage(img, sx * Tiles.getTileWidth(), sy * Tiles.getTileHeight());
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g, double x, double y, double z,
			World world, Collection<LightSource> lights, float sx, float sy) throws SlickException {
		renderTexture(getTexture(x, y, z, world), container, game, g, x, y, z, world, 1, lights, sx, sy);
	}

	public abstract String getTexture(double x, double y, double z, World world);

	public static float[] getLighting(World world, Collection<LightSource> lights, double x, double y) {
		Color wl = world.getLightColor();

		float red = wl.r;
		float green = wl.g;
		float blue = wl.b;

		float lum = (red + green + blue) / 3;

		float rscale = FastMath.max(lum * 10, 1);
		float gscale = FastMath.max(lum * 10, 1);
		float bscale = FastMath.max(lum * 10, 1);

		for (LightSource l : lights) {
			double dist = FastMath.hypot(x - l.getLX(), y - l.getLY());

			if (dist < 1) {
				dist = 1;
			}

			red += l.getR() / dist / FastMath.sqrt(dist) / rscale;
			green += l.getG() / dist / dist / gscale;
			blue += l.getB() / dist / dist / dist / bscale;
		}

		colors[0] = red;
		colors[1] = green;
		colors[2] = blue;

		return colors;
	}

	public boolean isFlying() {
		return false;
	}

	public final void setString(String name, String value) {
		if (strings.get(name) != null && value != null) {
			if (strings.get(name).equals(value)) {
				return;
			}
		}
		if (value == null) {
			value = "null";
		}
		ServerManager.getServer().updateEntity(new UString(id, name, value));
		strings.put(name, value);
	}

	public final String getString(String name) {
		String s = strings.get(name);
		if (s == null) {
			s = "null";
		}
		if (s.equals("null")) {
			return null;
		} else {
			return s;
		}
	}

	public final void setInt(String name, int value) {
		if (getInt(name) == value) {
			return;
		}
		ServerManager.getServer().updateEntity(new UInt(id, name, value));
		ints.put(name, value);
	}

	public final int getInt(String name) {
		Integer i = ints.get(name);
		if (i == null) {
			return 0;
		} else {
			return i;
		}
	}

	public void setLong(String name, long value) {
		if (getLong(name) == value) {
			return;
		}
		ServerManager.getServer().updateEntity(new ULong(id, name, value));
		longs.put(name, value);
	}

	public final long getLong(String name) {
		Long i = longs.get(name);
		if (i == null) {
			return 0;
		} else {
			return i;
		}
	}

	public final void setFloat(String name, float value) {
		if (getFloat(name) == value) {
			return;
		}
		ServerManager.getServer().updateEntity(new UFloat(id, name, value));
		floats.put(name, value);
	}

	public final float getFloat(String name) {
		Float i = floats.get(name);
		if (i == null) {
			return 0;
		} else {
			return i;
		}
	}

	public final void setBoolean(String name, boolean value) {
		if (getBoolean(name) == value) {
			return;
		}
		ServerManager.getServer().updateEntity(new UBoolean(id, name, value));
		bools.put(name, value);
	}

	public final boolean getBoolean(String name) {
		Boolean i = bools.get(name);
		if (i == null) {
			return false;
		} else {
			return i;
		}
	}

	public final void setDouble(String name, double value) {
		if (getDouble(name) == value) {
			return;
		}
		ServerManager.getServer().updateEntity(new UDouble(id, name, value));
		doubles.put(name, value);
	}

	public final double getDouble(String name) {
		Double i = doubles.get(name);
		if (i == null) {
			return 0;
		} else {
			return i;
		}
	}

	public final void setObject(String name, Object value) {
		if (getObject(name) == value) {
			return;
		}
		ServerManager.getServer().updateEntity(new UObject(id, name, value));
		objects.put(name, value);
	}

	public final Object getObject(String name) {
		return objects.get(name);
	}

	public final void pushObject(String name) {
		ServerManager.getServer().updateEntity(new UObject(id, name, getObject(name)));
	}

	public final long getID() {
		return id;
	}

	public final void reloadID() {
		id = getNextID();
	}
}
