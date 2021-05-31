package storage.server;

import java.net.*;
import java.util.HashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import storage.cmd.*;
import storage.*;

public final class ServerCmdReceiver {
	private HashMap<InetAddress, CmdBuilder> connections;

	private final int BUFFER_SIZE = 16384;
	private byte[] buffer;

	private DatagramSocket socket;
	private DatagramPacket packet;

	public ServerCmdReceiver (int port) {
		this.buffer = new byte[CommonConstants.CMD_PACKET_BUFFER_SIZE];

		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.err.println("Error while initializing receiver:\n" + e.getMessage());
			System.exit(1);
		}

		this.packet = new DatagramPacket(this.buffer, this.buffer.length);

		this.connections = new HashMap<InetAddress, CmdBuilder>();
	}

	public void processNextPacket () throws IOException {
		this.socket.receive(this.packet);

		InetAddress senderAddress = this.packet.getAddress();
		int length = this.packet.getLength();
		if (this.connections.containsKey(senderAddress)) {
			// This is already an open connection: append to its buffer
			CmdBuilder builder = this.connections.get(senderAddress);
			builder.append(this.buffer, length);
			NetworkedCmd cmd = builder.getCmd();
			if (cmd != null) {
				// The command has been assembled. run it and remove the connection
				this.runCommand(cmd);
				this.connections.remove(senderAddress);
			}
		} else {
			// A new connection
			ByteArrayInputStream byteStream = new ByteArrayInputStream(this.buffer, 0, this.buffer.length);
			ObjectInputStream objectStream = new ObjectInputStream(byteStream);
			final Integer commandLength;
			try {
				commandLength = (Integer) objectStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			}
			CmdBuilder builder = new CmdBuilder(commandLength);
			this.connections.put(senderAddress, builder);
		}
	}

	private void runCommand (NetworkedCmd cmd) {
		System.err.println("Received command " + cmd.getClass().getSimpleName());

		// Response response = cmd.runOnServer();
		// ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		// ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
		// objectStream.writeObject(response);
	}
}

class CmdBuilder {
	private byte[] buffer;
	private int size;
	private int targetSize;

	public CmdBuilder (int targetSize) {
		this.buffer = new byte[targetSize];
		this.size = 0;
		this.targetSize = targetSize;
	}

	public NetworkedCmd getCmd () {
		if (this.size < this.targetSize) {
			return null;
		}

		NetworkedCmd cmd = null;
		try {
			ByteArrayInputStream byteStream = new ByteArrayInputStream(this.buffer, 0, this.targetSize);
			ObjectInputStream objectStream = new ObjectInputStream(byteStream);
			cmd = (NetworkedCmd) objectStream.readObject();
		} catch (ClassNotFoundException e) {
			System.err.println("Assembled an invalid command:\n" + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("I/O error while reading command:\n" + e.getMessage());
			System.exit(1);
		}
		return cmd;
	}

	public void append (byte[] source, int length) {
		System.arraycopy(source, 0, this.buffer, this.size, length);
		this.size += length;
	}
}