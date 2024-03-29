package rpgonline.net.logon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.newdawn.slick.util.Log;

/**
 * A server using POST over HTTPS to perform login operations.
 * 
 * @author Tomas
 */
public class HttpsLogonServer implements UserServer {
	/**
	 * The URL and port number to connect to.
	 */
	private final String url;

	/**
	 * Constructs a connection to a HTTPS login server.
	 * 
	 * @param url A URL and port number separated by a {@code :}
	 */
	public HttpsLogonServer(String url) {
		if (url.startsWith("https://")) {
			this.url = url;
		} else {
			throw new IllegalArgumentException("\"" + url + "\" - URL must use protocol \"https\"."
					+ "If your URL uses \"http\" used HttpLogonServer instead.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogonStatus attemptLogon(String login, String password) {
		try {
			String query = "email=" + URLEncoder.encode(login, "UTF-8");
			query += "&";
			query += "password=" + URLEncoder.encode(password, "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("logon", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("Logon Resp Code: " + con.getResponseCode());
			System.out.println("Logon Resp Message: " + con.getResponseMessage());

			if (response.toString() == "logon") {
				return LogonStatus.SUCCESS;
			} else {
				return LogonStatus.INCORRECT;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return LogonStatus.ENCODE_FAILIURE;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return LogonStatus.INVALID_URL;
		} catch (IOException e) {
			e.printStackTrace();
			return LogonStatus.CONNECT_FAILIURE;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUp() {
		try {
			String domain = getDomainName(url);

			try (Socket socket = new Socket()) {
				socket.connect(new InetSocketAddress(domain, 80), 4000);
				return true;
			} catch (IOException e) {
				return false; // Either timeout or unreachable or failed DNS lookup.
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUserName(long uuid) {
		try {
			String query = "email=" + URLEncoder.encode(uuid + "", "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("username", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("User name Resp Code: " + con.getResponseCode());
			System.out.println("User name Resp Message: " + con.getResponseMessage());

			return response.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "{" + String.format("%04X", uuid & 0xFFFF000000000000L >>> 48) + "-"
					+ String.format("%04X", uuid & 0x0000FFFF00000000L >>> 32) + "-"
					+ String.format("%04X", uuid & 0x00000000FFFF0000L >>> 16) + "-"
					+ String.format("%04X", uuid & 0x000000000000FFFFL >>> 0) + "}";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getUserUuid(String login) {
		try {
			String query = "email=" + URLEncoder.encode(login, "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("uuid", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("User name Resp Code: " + con.getResponseCode());
			System.out.println("User name Resp Message: " + con.getResponseMessage());

			return Long.parseLong(response.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Gets a domain name from a URL
	 * 
	 * @param url a URL.
	 * @return A domain name.
	 * @throws MalformedURLException If the URL is not valid.
	 */
	public static String getDomainName(String url) throws MalformedURLException {
		if (!url.startsWith("http") && !url.startsWith("https")) {
			url = "http://" + url;
		}
		URL netUrl = new URL(url);
		String host = netUrl.getHost();
		if (host.startsWith("www")) {
			host = host.substring("www".length() + 1);
		}
		return host;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPrivateUuid(String login, String password) {
		try {
			String query = "email=" + URLEncoder.encode(login, "UTF-8");
			query += "&";
			query += "password=" + URLEncoder.encode(password, "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("privateuuid", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("User name Resp Code: " + con.getResponseCode());
			System.out.println("User name Resp Message: " + con.getResponseMessage());

			return response.toString();
		} catch (IOException e) {
			Log.error(e);
			return UserServerUtils.ERROR32;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValidPrivateUuid(long uuid, String puuid) {
		try {
			String query = "uuid=" + URLEncoder.encode(uuid + "", "UTF-8");
			query += "&";
			query += "puuid=" + URLEncoder.encode(puuid + "", "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("privateuuid", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("User name Resp Code: " + con.getResponseCode());
			System.out.println("User name Resp Message: " + con.getResponseMessage());

			return Boolean.parseBoolean(response.toString());
		} catch (IOException e) {
			Log.error(e);
			return false;
		}
	}

	@Override
	public long getIDFromName(String username) {
		try {
			String query = "username=" + URLEncoder.encode(username, "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("idfromname", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("User name Resp Code: " + con.getResponseCode());
			System.out.println("User name Resp Message: " + con.getResponseMessage());

			return Long.parseLong(response.toString());
		} catch (IOException e) {
			Log.error(e);
			return -1;
		}
	}

	@Override
	public String getToken(long uuid, String puuid) {
		try {
			String query = "uuid=" + URLEncoder.encode(uuid + "", "UTF-8");
			query += "&";
			query += "puuid=" + URLEncoder.encode(puuid + "", "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("gentoken", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("User name Resp Code: " + con.getResponseCode());
			System.out.println("User name Resp Message: " + con.getResponseMessage());

			return response.toString();
		} catch (IOException e) {
			Log.error(e);
			return UserServerUtils.ERROR64;
		}
	}

	@Override
	public LogonStatus attemptLogon(String token) {
		try {
			String query = "token=" + URLEncoder.encode(token, "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("logontoken", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("Logon Resp Code: " + con.getResponseCode());
			System.out.println("Logon Resp Message: " + con.getResponseMessage());

			if (response.toString() == "logon") {
				return LogonStatus.SUCCESS;
			} else {
				return LogonStatus.INCORRECT;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return LogonStatus.ENCODE_FAILIURE;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return LogonStatus.INVALID_URL;
		} catch (IOException e) {
			e.printStackTrace();
			return LogonStatus.CONNECT_FAILIURE;
		}
	}

	@Override
	public long getTokenUUID(String token) {
		try {
			String query = "token=" + URLEncoder.encode(token, "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("uuidtoken", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("User name Resp Code: " + con.getResponseCode());
			System.out.println("User name Resp Message: " + con.getResponseMessage());

			return Long.parseLong(response.toString());
		} catch (IOException e) {
			Log.error(e);
			return -1;
		}
	}

	@Override
	public String getTokenPUUID(String token) {
		try {
			String query = "token=" + URLEncoder.encode(token, "UTF-8");
			query += "&";
			query += "action=" + URLEncoder.encode("privateuuidtoken", "UTF-8");

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-length", String.valueOf(query.length()));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
			con.setDoOutput(true);
			con.setDoInput(true);

			DataOutputStream output = new DataOutputStream(con.getOutputStream());

			output.writeBytes(query);

			output.close();

			DataInputStream input = new DataInputStream(con.getInputStream());

			StringBuilder response = new StringBuilder();

			for (int c = input.read(); c != -1; c = input.read()) {
				response.append(c);
			}
			input.close();

			System.out.println("User name Resp Code: " + con.getResponseCode());
			System.out.println("User name Resp Message: " + con.getResponseMessage());

			return response.toString();
		} catch (IOException e) {
			Log.error(e);
			return UserServerUtils.ERROR32;
		}
	}
}
