package storage;

import java.io.Serializable;

public class Response implements Serializable {
	public final boolean success;
	public final String payload;

	public Response (boolean success) {
		this(success, null);
	}

	public Response (boolean success, String payload) {
		this.success = success;
		this.payload = payload;
	}

	@Override
	public String toString () {
		return (this.success ? "OK\n" : "The command failed\n")
		     + (this.payload == null ? "" : this.payload.toString());
	}
}