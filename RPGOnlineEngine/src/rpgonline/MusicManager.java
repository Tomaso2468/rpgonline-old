package rpgonline;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.util.Log;

/**
 * <p>
 * A class for loading, managing and switching between game music.
 * </p>
 * <p>
 * Additionally, this class also provides utilities for loading and playing
 * sound effects.
 * </p>
 * <p>
 * If a call using an invalid ID or referencing an unmapped {@code Music} or
 * {@code Sound} object is made then the call does not play any sound. For music
 * this stops the music and for sound this does nothing.
 * </p>
 * <p>
 * Any call to load music or sounds that use an already mapped ID will replace
 * that mapped ID. Overall, this class acts as two maps of {@code Sound} and
 * {@code Music} objects.
 * </p>
 * <p>
 * <b>Sounds mapped using {@code loadSound()} are not accessible from
 * {@code setMusic()} and music mapped using {@code loadMusic()} is not
 * accesible from {@code play()}</b>
 * </p>
 * 
 * @author Tomas
 */
public class MusicManager {
	/**
	 * An map of game-controlled IDs to {@code Music} objects.
	 */
	private static final Map<String, Music> music = new Hashtable<String, Music>();
	/**
	 * The currently playing {@code Music} object.
	 */
	private static Music current = null;
	/**
	 * An map of game-controlled IDs to {@code Sound} objects.
	 */
	private static final Map<String, Sound> sounds = new Hashtable<String, Sound>();

	/**
	 * Sets the currently playing {@code Music} object. This will stop any
	 * previously playing music.
	 * 
	 * @param id   A previously ID.
	 * @param loud A boolean value that determines the volume of the music.
	 *             {@code true} -&gt; {@code 0.9} and {@code false} -&gt;
	 *             {@code 0.4}
	 */
	public static void setMusic(String id, boolean loud) {
		if ((id == null || music.get(id) == null) && current != null) {
			current.fade(1000, 0, true);
			current = null;
		} else {
			if (id == null || music.get(id) == null) {
				return;
			}
			if (!music.get(id).playing()) {
				if (current != null) {
					current.fade(50, 0, true);
					new Thread() {
						public void run() {
							setUncaughtExceptionHandler(new GameExceptionHandler());
							try {
								Thread.sleep(60);
								music.get(id).loop(1, loud ? 1f : 0.5f);
								current = music.get(id);
							} catch (Exception e) {
								Log.error(e);
							}
						}
					}.start();
				} else {
					music.get(id).loop(1, loud ? 1f : 0.5f);
					current = music.get(id);
				}
			}
		}
	}

	/**
	 * Plays a sound effect from an ID.
	 * 
	 * @param id A previously ID.
	 */
	public static void playSound(String id) {
		if (sounds.get(id) == null) {
			return;
		}
		sounds.get(id).play();
	}

	/**
	 * Plays a sound effect from an ID with volume and pitch control.
	 * 
	 * @param id     A previously ID.
	 * @param pitch  The pitch to play the sound effect at.
	 * @param volume The volume to play the sound effect at.
	 */
	public static void play(String id, float pitch, float volume) {
		if (sounds.get(id) == null) {
			return;
		}
		sounds.get(id).play(pitch, volume);
	}

	/**
	 * Plays a sound effect from an ID at a specified position.
	 * 
	 * @param id A previously ID.
	 * @param x  The x position of the sound effect.
	 * @param y  The y position of the sound effect.
	 * @param z  The z position of the sound effect.
	 */
	public static void playAt(String id, float x, float y, float z) {
		if (sounds.get(id) == null) {
			return;
		}
		sounds.get(id).playAt(x, y, z);
	}

	/**
	 * Plays a sound effect from an ID at a specified position with volume and pitch
	 * control.
	 * 
	 * @param id     A previously mapped ID.
	 * @param x      The x position of the sound effect.
	 * @param y      The y position of the sound effect.
	 * @param z      The z position of the sound effect.
	 * @param pitch  The pitch to play the sound effect at.
	 * @param volume The volume to play the sound effect at.
	 */
	public static void playAt(String id, float pitch, float volume, float x, float y, float z) {
		if (sounds.get(id) == null) {
			return;
		}
		sounds.get(id).playAt(pitch, volume, x, y, z);
	}

	/**
	 * Plays a sound effect from an ID. When the effect ends the effect plays again.
	 * This will repeat until {@code stop()} is called.
	 * 
	 * @param id A previously ID.
	 */
	public static void loop(String id) {
		if (sounds.get(id) == null) {
			return;
		}
		sounds.get(id).loop();
	}

	/**
	 * Plays a sound effect from an ID with volume and pitch control. When the
	 * effect ends the effect plays again. This will repeat until {@code stop()} is
	 * called.
	 * 
	 * @param id     A previously mapped ID.
	 * @param pitch  The pitch to play the sound effect at.
	 * @param volume The volume to play the sound effect at.
	 */
	public static void loop(String id, float pitch, float volume) {
		if (sounds.get(id) == null) {
			return;
		}
		sounds.get(id).loop(pitch, volume);
	}

	/**
	 * Checks if a sound effect is playing.
	 * 
	 * @param id A previously mapped ID.
	 * @return {@code true} if the sound is playing, {@code false} otherwise.
	 */
	public static boolean playing(String id) {
		if (sounds.get(id) == null) {
			return false;
		}
		return sounds.get(id).playing();
	}

	/**
	 * Stops a sound effect.
	 * 
	 * @param id A previously mapped ID.
	 */
	public static void stop(String id) {
		if (sounds.get(id) == null) {
			return;
		}
		sounds.get(id).stop();
	}

	/**
	 * <p>
	 * Loads a sound from the {@code URL} and maps it to the specified ID.
	 * </p>
	 * <p>
	 * This method only supports .ogg files. Use {@code loadWav()} to load a .wav
	 * file. Additionally, to be able to use positional sound the audio must have
	 * only one audio channel.
	 * </p>
	 * 
	 * @param id  An unmapped ID.
	 * @param loc A url pointing to a .ogg file.
	 * @throws SlickException If an error occurred loading sound.
	 */
	public static void loadSound(String id, URL loc) throws SlickException {
		try {
			sounds.put(id, new Sound(loc.openStream(), ".ogg"));
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Loads a sound from the {@code URL} and maps it to the specified ID.
	 * </p>
	 * <p>
	 * This method only supports .wav files. Use {@code loadSound()} to load a .ogg
	 * file. Additionally, to be able to use positional sound the audio must have
	 * only one audio channel.
	 * </p>
	 * 
	 * @param id  An unmapped ID.
	 * @param loc A url pointing to a .wav file.
	 * @throws SlickException If an error occurred loading sound.
	 */
	public static void loadWav(String id, URL loc) throws SlickException {
		try {
			sounds.put(id, new Sound(new BufferedInputStream(loc.openStream()), ".wav"));
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Loads a sound from the {@code URL} and maps it to the specified ID.
	 * </p>
	 * <p>
	 * This method only supports .aif files. Additionally, to be able to use
	 * positional sound the audio must have only one audio channel.
	 * </p>
	 * 
	 * @param id  An unmapped ID.
	 * @param loc A url pointing to a .aif file.
	 * @throws SlickException If an error occurred loading sound.
	 */
	public static void loadAif(String id, URL loc) throws SlickException {
		try {
			sounds.put(id, new Sound(new BufferedInputStream(loc.openStream()), ".aif"));
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Loads a chiptune sound from the {@code URL} and maps it to the specified ID.
	 * </p>
	 * <p>
	 * This method only supports .xm files.
	 * </p>
	 * 
	 * @param id  An unmapped ID.
	 * @param loc A url pointing to a .xm file.
	 * @throws SlickException If an error occurred loading sound.
	 */
	public static void loadXM(String id, URL loc) throws SlickException {
		try {
			sounds.put(id, new Sound(new BufferedInputStream(loc.openStream()), ".xm"));
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Loads a chiptune sound from the {@code URL} and maps it to the specified ID.
	 * </p>
	 * <p>
	 * This method only supports .mod files.
	 * </p>
	 * 
	 * @param id  An unmapped ID.
	 * @param loc A url pointing to a .mod file.
	 * @throws SlickException If an error occurred loading sound.
	 */
	public static void loadMod(String id, URL loc) throws SlickException {
		try {
			sounds.put(id, new Sound(new BufferedInputStream(loc.openStream()), ".mod"));
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Loads music from the {@code URL} and maps it to the specified ID.
	 * </p>
	 * <p>
	 * The supported types are:
	 * </p>
	 * <ul>
	 * <li>.ogg</li>
	 * <li>.wav</li>
	 * <li>.aif</li>
	 * <li>.aiff</li>
	 * <li>.xm</li>
	 * <li>.mod</li>
	 * </ul>
	 * 
	 * @param id  An unmapped ID.
	 * @param loc A URL pointing to music.
	 * @throws SlickException If an error occurred loading the music.
	 */
	public static void loadMusic(String id, URL loc) throws SlickException {
		Log.debug("Loading track: " + id);
		music.put(id, new Music(loc));
	}
}
