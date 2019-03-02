package rpgonline.net.logon;

/**
 * A interface for managing login details.
 * 
 * @author Tomas
 * 
 * @see rpgonline.net.logon.HttpLogonServer
 * @see rpgonline.net.logon.HttpsLogonServer
 * @see rpgonline.net.logon.LocalLogonServer
 * @see rpgonline.net.logon.MultiLogonServer
 */
public interface UserServer {
	/**
	 * Gets the status of an attempted login.
	 * 
	 * @param login    The user name/email to connect with.
	 * @param password A valid password.
	 * @return A {@code LogonStatus} object representing the status of the request.
	 */
	public LogonStatus attemptLogon(String login, String password);

	/**
	 * Checks if the server is running or if the client can connect to the server.
	 * 
	 * @return {@code true} if the server is running. {@code false} otherwise.
	 */
	public boolean isUp();

	/**
	 * Gets a user name from a UUID.
	 * 
	 * @param uuid A user UUID.
	 * @return The user's name.
	 */
	public String getUserName(long uuid);

	/**
	 * Gets a UUID from a login.
	 * 
	 * @param login A user name/email.
	 * @return A valid UUID.
	 */
	public long getUserUuid(String login);

	/**
	 * Gets the private key for this user.
	 * 
	 * @param login    The user name/email to connect with.
	 * @param password A valid password.
	 * @return A valid UUID.
	 */
	public long getPrivateUuid(String login, String password);

	/**
	 * Checks if a UUID and private UUID is valid
	 * 
	 * @param uuid  A UUID.
	 * @param puuid A private UUID.
	 * @return {@code true} if the UUIDs are valid. {@code false} otherwise.
	 */
	public boolean isValidPrivateUuid(long uuid, long puuid);

	/**
	 * An enum representing connect status.
	 * 
	 * @author Tomas
	 */
	public static enum LogonStatus {
		CONNECT_FAILIURE(false), ENCODE_FAILIURE(false), LOGON_FAILIURE(false), MISC_FAILIURE(false),
		INVALID_URL(false), SUCCESS(true), INCORRECT(false);
		private final boolean valid;

		LogonStatus(boolean valid) {
			this.valid = valid;
		}

		/**
		 * Was the login successful.
		 * 
		 * @return {@code true} if this {@code enum} constant indicates a valid login.
		 *         {@code false} otherwise.
		 */
		public boolean isValid() {
			return valid;
		}
	}
}
