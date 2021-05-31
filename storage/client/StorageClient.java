package storage.client;

import java.io.IOException;
import java.net.*;
import storage.*;
import storage.cmd.*;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class StorageClient {
	DatagramSocket socket;
	SocketAddress address;

	private static StorageClient instance = null;

	private StorageClient (SocketAddress addr) {
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println("Cannot create socket");
			System.exit(1);
		}
		this.address = addr;
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
			try {
				final byte[] bufferCmd;
				{
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
					objStream.writeObject(cmd);
					bufferCmd = byteStream.toByteArray();
				}

				int packetCapacity = CommonConstants.CMD_PACKET_BUFFER_SIZE;
				int numPackets = (bufferCmd.length + packetCapacity - 1) / packetCapacity;

				final byte[] bufferNum;
				{
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
					objStream.writeObject(new Integer(bufferCmd.length));
					bufferNum = byteStream.toByteArray();
				}

				this.socket.send(new DatagramPacket(bufferNum, bufferNum.length, this.address));

				for (int i = 0; i < numPackets; i++) {
					int offset = packetCapacity * i;
					int packetSize = Math.min(bufferCmd.length - offset, packetCapacity);
					this.socket.send(new DatagramPacket(bufferCmd, offset, packetSize, this.address));
				}
			} catch (IOException e) {
				System.err.println("I/O exception while sending request:\n");
				e.printStackTrace();
			}
		}

		this.history.push(arguments[0]);
		return success;
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
