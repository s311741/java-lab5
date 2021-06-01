package storage;

public class CommonConstants {
	// A single packet MUST still be enough to send an Integer, which is ~81 bytes
	public static final int PACKET_BUFFER_SIZE = 16384;
	public static final int PACKET_TIMEOUT_MILLISECONDS = 5000;

	private CommonConstants () { }
}