package rpgonline.net;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.util.Log;

import rpgonline.GameExceptionHandler;
import rpgonline.world.Entity;
import rpgonline.world.LightSource;
import rpgonline.world.World;

/**
 * In interface describing a game server.
 * 
 * @author Tomas
 */
public interface Server {
	/**
	 * Gets the game world.
	 * 
	 * @return A {@code World} object.
	 */
	public World getWorld();

	/**
	 * Gets the {@code x} position of the player measured in tiles.
	 * 
	 * @return A non-NaN non-Infinity double value.
	 */
	public double getX();

	/**
	 * Gets the {@code y} position of the player measured in tiles.
	 * 
	 * @return A non-NaN non-Infinity double value.
	 */
	public double getY();

	/**
	 * Accelerate the player in the x direction.
	 * 
	 * @param mx A non-NaN non-Infinity double value.
	 */
	public void moveX(double mx);

	/**
	 * Accelerate the player in the y direction.
	 * 
	 * @param my A non-NaN non-Infinity double value.
	 */
	public void moveY(double my);

	/**
	 * Returns the speed the player should move at.
	 * 
	 * @return A non-NaN non-Infinity double value.
	 */
	public double getPlayerSpeed();

	/**
	 * Returns a multiplier which should be used when sprinting.
	 * 
	 * @return A non-NaN non-Infinity positive double value.
	 */
	public double getSprintMultiplier();

	/**
	 * Starts the server.
	 */
	public default void start() {
		new Thread("Server Thread") {
			public void run() {
				Thread.currentThread().setUncaughtExceptionHandler(new GameExceptionHandler());
				
				long last_update = System.currentTimeMillis();

				try {
					init();
				} catch (Throwable e) {
					Log.error("Could not start server.", e);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					Log.info("Retrying server start.");
					try {
						init();
					} catch (Throwable e2) {
						Log.error("Server start failed again. Aborting.", e2);
						return;
					}
				}
				try {
					while (true) {
						last_update = System.currentTimeMillis();
						update();
						if (System.currentTimeMillis() - last_update > (1000 / 60)) {
							if (System.currentTimeMillis() - last_update - 1000 / 60 > 32) {
								Log.warn("Server is running " + (System.currentTimeMillis() - last_update - 1000 / 60)
										+ " millis behind.");
							}
						}
						while (System.currentTimeMillis() - last_update < (1000 / 60)) {
							Thread.yield();
						}
					}
				} catch (Throwable e) {
					Log.error("Error in server thread.", e);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					Log.info("Re-entering server loop.");
					try {
						while (true) {
							last_update = System.currentTimeMillis();
							update();
							if (System.currentTimeMillis() - last_update > (1000 / 60)) {
								if (System.currentTimeMillis() - last_update - 1000 / 60 > 32) {
									Log.warn("Server is running "
											+ (System.currentTimeMillis() - last_update - 1000 / 60)
											+ " millis behind.");
								}
							}
							while (System.currentTimeMillis() - last_update < (1000 / 60)) {
								Thread.yield();
							}
						}
					} catch (Throwable e2) {
						Log.error(
								"Server loop failed again. Assuming the error will repeat indefinetly. Stopping Server.",
								e2);

					}
				}
			}
		}.start();
	}

	/**
	 * Gets a list of all entities on this server.
	 * 
	 * @return A list of entities.
	 */
	public default Collection<Entity> getEntities() {
		return Collections.emptyList();
	}

	/**
	 * Initialise the server.
	 */
	public default void init() {

	}

	/**
	 * Perform when server tick.
	 */
	public default void update() {
		getWorld().doUpdateServer();
	}

	/**
	 * Perform a task client-side.
	 * 
	 * @param clientID The id of the client to perform the action on.
	 * @param command  The command to perform.
	 */
	public void doOnClient(long clientID, String command);

	/**
	 * Performs a task server-side
	 * 
	 * @param req The command to perform.
	 * @return The return value of the task.
	 */
	public default Object request(String req) {
		throw new UnsupportedOperationException("Server command " + req + " is not supported.");
	}

	/**
	 * Add an entity to the server.
	 * 
	 * @param e The entity to add.
	 */
	public void addEntity(Entity e);

	/**
	 * Removes an entity from the server.
	 * 
	 * @param e The entity to remove.
	 */
	public void removeEntity(Entity e);

	/**
	 * Gets a list of all lights in the world.
	 * 
	 * @return A {@code List} object of lights.
	 */
	public List<LightSource> getLights();

	/**
	 * Determines is the server is running with a GUI.
	 * 
	 * @return {@code true} if the server is running with a GUI. {@code false}
	 *         otherwise.
	 */
	public boolean hasGUI();

	/**
	 * The {@code x} position of the sun.
	 * 
	 * @return A float value between {@code -Infinity} and {@code +Infinity}.
	 */
	public float getSunX();

	/**
	 * Gets the colour of the sun.
	 * 
	 * @return A colour object.
	 */
	public Color getSunColor();

	/**
	 * Gets the colour of the sky.
	 * 
	 * @return A colour object.
	 */
	public Color getSkyColor();

	/**
	 * Gets the music the client should play.
	 * 
	 * @return A non-null string mapped to music in {@code MusicManager}.
	 * @see rpgonline.MusicManager
	 */
	public String getMusic();
	
	public void stop();
}
