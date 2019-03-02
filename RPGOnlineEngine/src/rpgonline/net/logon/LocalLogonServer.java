package rpgonline.net.logon;

import java.util.ArrayList;
import java.util.List;

/**
 * A client side logon server that should be used <b>Only for testing</b>
 * 
 * @author Tomas
 *
 */
public class LocalLogonServer implements UserServer {
	/**
	 * A list of users.
	 */
	private final List<User> users;

	/**
	 * Constructs a login server from a list of users.
	 * 
	 * @param users A list of users.
	 */
	public LocalLogonServer(List<User> users) {
		this.users = users;
	}

	/**
	 * Constructs a login server.
	 */
	public LocalLogonServer() {
		this(new ArrayList<User>());
	}

	/**
	 * Adds a user to the server.
	 * 
	 * @param u The user to add.
	 */
	public synchronized void add(User u) {
		users.add(u);
	}

	/**
	 * Removes a user from the server.
	 * 
	 * @param u The user to remove.
	 */
	public synchronized void remove(User u) {
		users.remove(u);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized LogonStatus attemptLogon(String login, String password) {
		for (User u : users) {
			if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
				return LogonStatus.SUCCESS;
			}
		}
		return LogonStatus.INCORRECT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean isUp() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized String getUserName(long uuid) {
		for (User u : users) {
			if (u.getUuid() == uuid) {
				return u.getUsername();
			}
		}
		return "{" + String.format("%04X", uuid & 0xFFFF000000000000L >>> 48) + "-"
				+ String.format("%04X", uuid & 0x0000FFFF00000000L >>> 32) + "-"
				+ String.format("%04X", uuid & 0x00000000FFFF0000L >>> 16) + "-"
				+ String.format("%04X", uuid & 0x000000000000FFFFL >>> 0) + "}";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized long getUserUuid(String login) {
		for (User u : users) {
			if (u.getLogin() == login) {
				return u.getUuid();
			}
		}
		return -1;
	}

	/**
	 * A class representing a user.
	 * 
	 * @author Tomas
	 */
	public static class User {
		/**
		 * A user name or email.
		 */
		private final String login;
		/**
		 * A password.
		 */
		private final String password;
		/**
		 * The user's ID.
		 */
		private final long uuid;
		/**
		 * A user name.
		 */
		private final String username;
		/**
		 * A private ID for verification.
		 */
		private final long puuid;

		/**
		 * Constructs a new {@code User}.
		 * 
		 * @param login    A email or user name.
		 * @param password A password.
		 * @param uuid     A user ID.
		 * @param username A user name.
		 * @param puuid    A private ID.
		 */
		public User(String login, String password, long uuid, String username, long puuid) {
			this.login = login;
			this.password = password;
			this.uuid = uuid;
			this.username = username;
			this.puuid = puuid;
		}

		/**
		 * Constructs a new {@code User}. This constructor will auto generate private
		 * UUIDs.
		 * 
		 * @param login    A email or user name.
		 * @param password A password.
		 * @param uuid     A user ID.
		 * @param username A user name.
		 */
		public User(String login, String password, long uuid, String username) {
			this.login = login;
			this.password = password;
			this.uuid = uuid;
			this.username = username;
			long hpuuid = (("" + uuid + login).hashCode() << 32L) | (("" + password + username).hashCode());
			if (hpuuid == -1) {
				this.puuid = Long.MAX_VALUE;
			} else {
				this.puuid = hpuuid;
			}
		}

		/**
		 * Gets the user's login.
		 * 
		 * @return An email or user name.
		 */
		public String getLogin() {
			return login;
		}

		/**
		 * Gets the user's password.
		 * 
		 * @return A password.
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * Gets the user's ID.
		 * 
		 * @return A unique long value.
		 */
		public long getUuid() {
			return uuid;
		}

		/**
		 * Gets the user's name.
		 * 
		 * @return A user name.
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * Gets the private verification ID for this user.
		 * 
		 * @return A unique long value.
		 */
		public long getPuuid() {
			return puuid;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getPrivateUuid(String login, String password) {
		for (User u : users) {
			if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
				return u.getPuuid();
			}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValidPrivateUuid(long uuid, long puuid) {
		for (User u : users) {
			if (u.getUuid() == uuid && u.getPuuid() == puuid) {
				return true;
			}
		}
		return false;
	}
}
