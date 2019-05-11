package rpgonline.tile;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for managing tiles.
 * 
 * @author Tomas
 */
public class Tiles {
	/**
	 * A tile for if no tile can be found.
	 */
	public static final Tile ERRORED_TILE = Tile.EMPTY_TILE;
	/**
	 * A list of tiles.
	 */
	static final List<Tile> tiles = new ArrayList<Tile>();
	/**
	 * The width of a tile.
	 */
	private static float tile_width = 0;
	/**
	 * The height of a tile.
	 */
	private static float tile_height = 0;

	/**
	 * Gets a tile from its ID.
	 * 
	 * @param id A valid tile ID.
	 * @return A tile or {@code ERRORED_TILE} if that tile doesn't exist.
	 */
	public static synchronized Tile getByID(int id) {
		for (Tile t : tiles) {
			if (t.getID() == id) {
				return t;
			}
		}
		return ERRORED_TILE;
	}

	/**
	 * Sets up the {@code Tiles} class.
	 * 
	 * @param width  The width of a tile.
	 * @param height The height of a tile.
	 */
	public static synchronized void init(float width, float height) {
		tile_width = width;
		tile_height = height;
	}

	/**
	 * Gets the width of tile.
	 * 
	 * @return A non-zero non-infinity positive float.
	 */
	public static final float getTileWidth() {
		return tile_width;
	}

	/**
	 * Gets the height of tile.
	 * 
	 * @return A non-zero non-infinity positive float.
	 */
	public static final float getTileHeight() {
		return tile_height;
	}

	/**
	 * Prints a list of all tiles.
	 */
	public static void printIDs() {
		for (Tile t : tiles) {
			System.out.println(t.getClass() + "\t" + t.getID());
		}
	}
}
