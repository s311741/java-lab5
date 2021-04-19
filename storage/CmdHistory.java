package storage;

import java.util.Iterator;

public final class CmdHistory extends Cmd {

	public CmdHistory (String[] arguments) { super(arguments); }

	@Override
	public boolean run () {
		System.out.println("Last " + Integer.toString(history.getSize()) + " commands:");
		for (String s: history) {
			System.out.println(s);
		}
		return true;
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

		/**
		 * Invalidates iterators
		 */
		public void push (String s) {
			if (this.size < this.capacity) {
				this.size++;
			}
			this.buffer[this.next] = s;
			if (++this.next >= this.capacity)
				this.next = 0;
		}

		class HCBIter implements Iterator<String> {
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
	private static HistoryCircularBuffer history = new HistoryCircularBuffer(13);
	public static void pushEntry (String s) { history.push(s); }
}
