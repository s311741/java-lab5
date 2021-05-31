package storage.server;

import java.net.*;
import java.util.HashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import storage.cmd.*;
import storage.*;

public final class ServerCmdReceiver {
	private HashMap<InetAddress, CmdBuilder> connections;

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
				this.runCommand(cmd, senderAddress, this.packet.getPort());
				this.connections.remove(senderAddress);
			}
		} else {
			// A new connection
			ByteArrayInputStream byteStream = new ByteArrayInputStream(this.buffer, 0,
			                                                           this.buffer.length);
			ObjectInputStream objectStream = new ObjectInputStream(byteStream);

			final int commandLength;
			try {
				commandLength = (int) (Integer) objectStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			}

			CmdBuilder builder = new CmdBuilder(commandLength);
			this.connections.put(senderAddress, builder);
		}
	}

	private void runCommand (NetworkedCmd cmd, InetAddress senderAddress, int port) throws IOException {
		System.err.println("Received command " + cmd.getClass().getSimpleName());

		Response response = cmd.runOnServer();

		// Send response
		final byte[] bufferResponse;
		{
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(response);
			bufferResponse = byteStream.toByteArray();
		}

		final byte[] bufferNum;
		{
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(new Integer(bufferResponse.length));
			bufferNum = byteStream.toByteArray();
		}
		this.socket.send(new DatagramPacket(bufferNum, bufferNum.length,
		                                    senderAddress, port));

		final int packetCapacity = CommonConstants.CMD_PACKET_BUFFER_SIZE;
		int numPackets = (bufferResponse.length + packetCapacity - 1) / packetCapacity;

		for (int i = 0; i < numPackets; i++) {
			int offset = packetCapacity * i;
			int packetSize = Math.min(bufferResponse.length - offset, packetCapacity);
			this.socket.send(new DatagramPacket(bufferResponse, offset, packetSize,
			                                    senderAddress, port));
		}
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