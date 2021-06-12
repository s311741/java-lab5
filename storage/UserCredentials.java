package storage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.io.Serializable;

public final class UserCredentials implements Serializable {
	private String name;
	private String password;

	public UserCredentials (String name, String hash) {
		this.name = name;
		this.password = hash;
	}

	public String getName () {
		return this.name;
	}

	public String getHash () {
		MessageDigest hasher = null;
		try {
			hasher = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) { }
		hasher.reset();
		byte[] buffer = hasher.digest((this.name + this.password).getBytes());
		BigInteger num = new BigInteger(1, buffer);
		String result = num.toString();

		StringBuilder sb = new StringBuilder();
		for (int i = result.length(); i < 32; i++) {
			sb.append('0');
		}
		return sb.append(result).toString();
	}
}