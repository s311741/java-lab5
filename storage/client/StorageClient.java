package storage.client;

import java.io.IOException;
import java.net.*;
import storage.*;
import storage.cmd.*;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class StorageClient {
	private DatagramSocket socket;
	private SocketAddress address;

	private DatagramPacket responsePacket;
	private byte[] responseBuffer;

	private static StorageClient instance = null;

	private StorageClient (SocketAddress addr) {
		try {
			this.socket = new DatagramSocket();
			this.socket.setSoTimeout(CommonConstants.PACKET_TIMEOUT_MILLISECONDS);
		} catch (SocketException e) {
			System.err.println("Cannot create socket:\n");
			e.printStackTrace();
			System.exit(1);
		}
		this.address = addr;

		this.responseBuffer = new byte[CommonConstants.PACKET_BUFFER_SIZE];
		this.responsePacket = new DatagramPacket(this.responseBuffer, this.responseBuffer.length);
	}

	public static StorageClient getClient () {
		if (instance == null) {
			System.err.println("Tried to access storage client before initialization");
			System.exit(1);
		}
		return instance;
	}

	public static void initialize (SocketAddress addr) {
		if (instance != null) {
			System.err.println("Tried to initialize storage client twice");
			System.exit(1);
		}
		instance = new StorageClient(addr);
	}

	public boolean runCommand (String[] arguments, Prompter prompter) {
		Cmd cmd = Cmd.getCommandFromWords(arguments, prompter);
		boolean success = cmd.runOnClient(arguments, prompter);
		if (!success) {
			return false;
		}

		if (cmd instanceof NetworkedCmd) {
			final Response response;
			try {
				this.sendNetworkedCmd((NetworkedCmd) cmd);
				response = this.receiveResponse();
			} catch (SocketTimeoutException e) {
				System.err.println("Response from server timed out: cannot be sure the command went through");
				return false;
			} catch (IOException e) {
				System.err.println("I/O exception while sending request:\n");
				e.printStackTrace();
				return false;
			}

			System.out.println(response);
		}

		this.history.push(arguments[0]);
		return success;
	}

	private void sendNetworkedCmd (NetworkedCmd cmd) throws IOException {
		// Send the command:

		// serialize the command object
		final byte[] bufferCmd;
		{
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(cmd);
			bufferCmd = byteStream.toByteArray();
		}

		// first, send the sum size of further packets
		final byte[] bufferNum;
		{
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(new Integer(bufferCmd.length));
			bufferNum = byteStream.toByteArray();
		}
		this.socket.send(new DatagramPacket(bufferNum, bufferNum.length, this.address));

		// send the command, in however many packets required
		final int packetCapacity = CommonConstants.PACKET_BUFFER_SIZE;
		int numPackets = (bufferCmd.length + packetCapacity - 1) / packetCapacity;

		for (int i = 0; i < numPackets; i++) {
			int offset = packetCapacity * i;
			int packetSize = Math.min(bufferCmd.length - offset, packetCapacity);
			this.socket.send(new DatagramPacket(bufferCmd, offset, packetSize, this.address));
		}
	}

	private Response receiveResponse () throws IOException, SocketTimeoutException {
		// get the number of packets
		this.socket.receive(this.responsePacket);
		final int responseSize;
		{
			ByteArrayInputStream byteStream = new ByteArrayInputStream(this.responseBuffer, 0,
			                                                           this.responseBuffer.length);
			ObjectInputStream objectStream = new ObjectInputStream(byteStream);
			try {
				responseSize = (int) (Integer) objectStream.readObject();
			} catch (ClassNotFoundException e) {
				System.err.println("Invalid response header received from server.");
				e.printStackTrace();
				return null;
			}
		}

		// get the full response, in however many packets
		byte[] fullResponse = new byte[responseSize];
		for (int currentSize = 0, length;
		     currentSize < responseSize;
		     currentSize += length) {
			this.socket.receive(this.responsePacket);
			length = this.responsePacket.getLength();
			System.arraycopy(this.responseBuffer, 0, fullResponse, currentSize, length);
		}

		final Response result;
		{
			ByteArrayInputStream byteStream = new ByteArrayInputStream(fullResponse, 0,
			                                                           fullResponse.length);
			ObjectInputStream objectStream = new ObjectInputStream(byteStream);
			try {
				result = (Response) objectStream.readObject();
			} catch (ClassNotFoundException e) {
				System.err.println("Invalid response body received from server.");
				e.printStackTrace();
				return null;
			}
		}

		return result;
	}

	private static class HistoryCircularBuffer implements Iterable<String> {
		private int size;
		private int capacity;
		private int next;
		private String[] buffer;

		public HistoryCircularBuffer (int capacity) {
			this.capacity = capacity;
			this.buffer = new String[this.capacity];
			this.size = 0;
			this.next = 0;
		}

		private int getSize () { return this.size; }

		/* Invalidates iterators! */
		public void push (String s) {
			if (this.size < this.capacity) {
				this.size++;
			}
			this.buffer[this.next] = s;
			if (++this.next >= this.capacity)
				this.next = 0;
		}

		public class HCBIter implements Iterator<String> {
			private int traversed = 0;
			public boolean hasNext () {
				return this.traversed < HistoryCircularBuffer.this.size;
			}
			public String next () {
				int capacity = HistoryCircularBuffer.this.capacity;
				int next = HistoryCircularBuffer.this.next;
				this.traversed++;
				return HistoryCircularBuffer.this.buffer[(capacity + next - this.traversed) % capacity];
			}
		}
		public Iterator iterator () { return this.new HCBIter(); }
	}
	private HistoryCircularBuffer history = new HistoryCircularBuffer(13);

	public void printHistory () {
		System.out.println("Last " + history.getSize() + " commands (latest first):");
		for (String line: this.history) {
			System.out.println(line);
		}
	}
}
