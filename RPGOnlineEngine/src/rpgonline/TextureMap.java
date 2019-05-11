package rpgonline;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.Log;

/**
 * <p>
 * A class for storing texture data.
 * </p>
 * <p>
 * Any call to load textures that use an already mapped ID will replace that
 * mapped ID. Overall, this class acts as a map of {@code Image} objects..
 * </p>
 * 
 * @author Tomas
 */
public class TextureMap {
	private static Map<String, Image> textures = new HashMap<String, Image>();

	/**
	 * Gets a texture from a texture ID.
	 * 
	 * @param s A texture ID.
	 * @return The mapped texture.
	 */
	public static Image getTexture(String s) {
		return textures.get(s.intern());
	}

	/**
	 * Adds an image to the texture map.
	 * 
	 * @param s   The texture ID to bind to.
	 * @param img An image.
	 */
	public static void addTexture(String s, Image img) {
		textures.put(s.intern(), img);
	}

	/**
	 * Loads a texture then binds it to a texture ID.
	 * 
	 * @param s   The texture ID to bind to.
	 * @param loc A URL pointing to a .png image.
	 * @throws SlickException If an error occurs loading an image.
	 */
	public static void loadTexture(String s, URL loc) throws SlickException {
		Log.debug("Loading texture " + s + " from " + loc);
		Texture tex;
		try {
			tex = TextureLoader.getTexture("PNG", new BufferedInputStream(loc.openStream()));
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException(e.toString());
		}
		Image img = new Image(tex);
		img.setFilter(Image.FILTER_NEAREST);

		addTexture(s, img);
	}

	/**
	 * <p>
	 * Adds an image as a sprite map.
	 * </p>
	 * <p>
	 * The textures are bounds as {@code s + "." + i} where i is the index in the
	 * sprite map starting at the top left corner and travelling horizontally.
	 * <p>
	 * 
	 * @param s   The prefix of the texture ID to bind to.
	 * @param img The image holding the sprite map.
	 * @param tw  The width of one sprite.
	 * @param th  The height of one sprite.
	 */
	public static void addSpriteMap(String s, Image img, int tw, int th) {
		SpriteSheet map = new SpriteSheet(img, tw, th);
		int id = 0;
		Log.debug("Map size " + map.getHorizontalCount() + " x " + map.getVerticalCount());
		for (int y = 0; y < map.getVerticalCount(); y++) {
			for (int x = 0; x < map.getHorizontalCount(); x++) {
				Log.debug("Loading sprite map part " + s + "." + id + " @ " + x + " " + y);
				addTexture(s + "." + id, map.getSprite(x, y));
				id += 1;
			}
		}
	}

	/**
	 * <p>
	 * Loads a texture then adds it as a sprite map.
	 * </p>
	 * <p>
	 * The textures are bounds as {@code s + "." + i} where i is the index in the
	 * sprite map starting at the top left corner and travelling horizontally.
	 * <p>
	 * 
	 * @param s   The prefix of the texture ID to bind to.
	 * @param loc A URL pointing to a .png image.
	 * @param tw  The width of one sprite.
	 * @param th  The height of one sprite.
	 * @throws SlickException if an error occurs loading the sprite map.
	 */
	public static void loadSpriteMap(String s, URL loc, int tw, int th) throws SlickException {
		Log.debug("Loading sprite map texture " + s + " from " + loc);
		Texture tex;
		try {
			tex = TextureLoader.getTexture("PNG", new BufferedInputStream(loc.openStream()));
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException(e.toString());
		}
		Image img = new Image(tex);
		img.setFilter(Image.FILTER_NEAREST);

		addSpriteMap(s, img, tw, th);
	}
}
