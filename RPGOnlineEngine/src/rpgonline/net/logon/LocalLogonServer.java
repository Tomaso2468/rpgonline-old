package rpgonline.net.logon;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.util.Log;

/**
 * A client side logon server that should be used <b>Only for testing</b>
 * 
 * @author Tomas
 *
 */
public class LocalLogonServer implements UserServer {
	private static final int PUUID_SIZE = 32;
	private static final int TOKEN_SIZE = 64;

	private final File database;
	private final List<User> users;

	public LocalLogonServer(File database) {
		this(new ArrayList<User>(), database);
	}

	public LocalLogonServer(List<User> users, File database) {
		database.mkdirs();
		this.database = database;
		this.users = users;
	}

	public LocalLogonServer(String gameID) {
		this(new ArrayList<User>(), gameID);
	}

	public LocalLogonServer(List<User> users, String gameID) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			database = new File(new File(System.getenv("APPDATA"), "rpgonline"), gameID);
		} else if (os.contains("mac")) {
			database = new File(new File(System.getProperty("user.home") + "/Library/Application Support", "rpgonline"),
					gameID);
		} else {
			database = new File(new File(System.getProperty("user.home"), ".rpgonline"), gameID);
		}

		database.mkdirs();
		this.users = users;
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
			if (u.getLogin().equals(login)) {
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
		public final String login;
		/**
		 * A password.
		 */
		public final String password;
		/**
		 * The user's ID.
		 */
		public final long uuid;
		/**
		 * A user name.
		 */
		public final String username;
		/**
		 * A private ID for verification.
		 */
		public String puuid;

		public long reload_time;

		/**
		 * Constructs a new {@code User}.
		 * 
		 * @param login    A email or user name.
		 * @param password A password.
		 * @param uuid     A user ID.
		 * @param username A user name.
		 * @param puuid    A private ID.
		 */
		public User(String login, String password, long uuid, String username, String puuid) {
			this.login = login;
			this.password = password;
			this.uuid = uuid;
			this.username = username;
			this.puuid = puuid;
			this.reload_time = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 5;
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
			this.reload_time = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 5;
			byte[] puuid_bytes = new byte[PUUID_SIZE];
			new SecureRandom().nextBytes(puuid_bytes);
			this.puuid = UserServerUtils.bytesToHex(puuid_bytes);
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
		public String getPuuid() {
			if (reload_time < System.currentTimeMillis()) {
				pickNewPUUID();
			}
			return puuid;
		}

		public void pickNewPUUID() {
			byte[] puuid_bytes = new byte[PUUID_SIZE];
			new SecureRandom().nextBytes(puuid_bytes);
			this.puuid = UserServerUtils.bytesToHex(puuid_bytes);

			this.reload_time = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 5;
		}
	}

	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPrivateUuid(String login, String password) {
		for (User u : users) {
			if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
				return u.getPuuid();
			}
		}
		return UserServerUtils.ERROR64;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValidPrivateUuid(long uuid, String puuid) {
		for (User u : users) {
			if (u.getUuid() == uuid && u.getPuuid().equals(puuid)) {
				return true;
			}
		}
		return false;
	}

	private long count = -1;

	public synchronized long getNextID() {
		count += 1;

		long next = new SecureRandom(longToBytes(count)).nextLong();

		for (User u : users) {
			if (u.uuid == next) {
				return getNextID();
			}
		}

		return next;
	}

	@Override
	public synchronized long getIDFromName(String username) {
		for (User u : users) {
			if (u.getUsername().equals(username)) {
				return u.getUuid();
			}
		}
		return -1;
	}

	private static class Token {
		public long uuid;
		public String token;

		public Token(long uuid, String token) {
			super();
			this.uuid = uuid;
			this.token = token;
		}
	}

	@Override
	public synchronized String getToken(long uuid, String puuid) {
		if (isValidPrivateUuid(uuid, puuid)) {
			return createToken(uuid).token;
		}
		return UserServerUtils.bytesToHex(UserServerUtils.genErrorUUID(TOKEN_SIZE));
	}

	@Override
	public synchronized LogonStatus attemptLogon(String token) {
		for (Token t : getTokens()) {
			if (t.token.equals(token)) {
				return LogonStatus.SUCCESS;
			}
		}
		return LogonStatus.INCORRECT;
	}

	@Override
	public synchronized long getTokenUUID(String token) {
		for (Token t : getTokens()) {
			if (t.token.equals(token)) {
				return t.uuid;
			}
		}

		return -1;
	}

	@Override
	public synchronized String getTokenPUUID(String token) {
		for (Token t : getTokens()) {
			if (t.token.equals(token)) {
				for (User u : users) {
					if (u.uuid == t.uuid) {
						return u.getPuuid();
					}
				}
			}
		}

		return UserServerUtils.bytesToHex(UserServerUtils.genErrorUUID(TOKEN_SIZE));
	}

	private synchronized List<Token> getTokens() {
		List<Token> tokens = new ArrayList<Token>();

		File f = new File(database, "tokens.dat");

		try {
			if (f.exists()) {
				List<String> lines = Files.readAllLines(f.toPath());

				for (String s : lines) {
					if (!s.trim().equals("")) {
						String[] sa = s.split(" ");

						tokens.add(new Token(Long.parseLong(sa[0]), sa[1]));
					}
				}
			}
		} catch (IOException e) {
			Log.error(e);
		}

		return tokens;
	}

	private synchronized Token createToken(long uuid) {
		Token t = new Token(uuid, UserServerUtils.bytesToHex(UserServerUtils.generateSecure(TOKEN_SIZE)));

		for (Token t2 : getTokens()) {
			if (t.token.equals(t2.token)) {
				return createToken(uuid);
			}
		}

		File f = new File(database, "tokens.dat");

		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			List<String> lines = Files.readAllLines(f.toPath());

			lines.add(t.uuid + " " + t.token);

			PrintWriter pw = new PrintWriter(f);

			for (String s : lines) {
				pw.println(s);
			}

			pw.flush();
			pw.close();
		} catch (IOException e) {
			Log.error(e);
		}

		return t;
	}
}