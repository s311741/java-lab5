package storage;

import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Request {
	public static enum Type {
		SHUTDOWN,
		ADD,
		GET_MINIMUM,
		GET_INFO,
		REMOVE_BY_ID,
		REMOVE_LOWER,
	}

	public static final int MAX_PACKET_LENGTH = 16384;

	public final Type type;

	public Request (Type type) {
		this.type = type;
	}

	public byte[] serialize () throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(MAX_PACKET_LENGTH);
		ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
		objStream.writeInt(this.type.ordinal());
		objStream.flush();

		if (byteStream.size() > MAX_PACKET_LENGTH) {
			throw new Error("Packet too large");
		}

		return byteStream.toByteArray();
	}

	public static Request deserialize (byte[] buffer, int length) throws IOException {
		if (length < 1 || length > MAX_PACKET_LENGTH) {
			return null;
		}

		ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(buffer, 0, length));

		Type type = Type.values()[objStream.readInt()];
		Request rq = new Request(type);

		return rq;
	}
}