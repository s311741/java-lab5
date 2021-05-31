package storage;

public class Response {
	public final boolean success;
	public final Object payload;

	public Response (boolean success) {
		this(success, null);
	}
	public Response (boolean success, Object payload) {
		this.success = success;
		this.payload = payload;
	}
}