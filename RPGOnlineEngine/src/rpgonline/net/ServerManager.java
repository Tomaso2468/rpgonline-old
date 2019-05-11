package rpgonline.net;

import rpgonline.net.logon.UserServer;

/**
 * A class for managing server access.
 * 
 * @author Tomas
 */
public final class ServerManager {
	static long update_time = 1000000000 / 60;
	/**
	 * The currently running/connected server.
	 */
	private static Server server;
	/**
	 * The server providing login support.
	 */
	private static UserServer user_server;

	/**
	 * Gets the currently running/connected server.
	 * 
	 * @return A {@code Server} object or {@code null} if no server is running.
	 */
	public static Server getServer() {
		return server;
	}

	/**
	 * Sets the currently running/connected server
	 * 
	 * @param server A non-null {@code Server} object.
	 */
	public static void setServer(Server server) {
		ServerManager.server = server;
	}

	/**
	 * Gets a server providing login support.
	 * 
	 * @return A {@code UserServer} object or {@code null} if no {@code UserServer}
	 *         is bound.
	 */
	public static UserServer getUserServer() {
		return user_server;
	}

	/**
	 * Sets the server providing login support.
	 * 
	 * @param user_server A {@code UserServer} object.
	 */
	public static void setUserServer(UserServer user_server) {
		ServerManager.user_server = user_server;
	}

	public static double getTPS() {
		return 1000000000.0 / update_time;
	}
}
