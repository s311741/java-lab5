package storage;

import java.io.BufferedReader;
import java.io.Writer;
import java.io.IOException;
import java.util.Stack;

public final class Prompter {
	private BufferedReader reader;
	private Writer writer;
	private Stack<String> prefixes;

	public Prompter (BufferedReader reader, Writer writer) {
		this.reader = reader;
		this.writer = writer;
		this.prefixes = new Stack<String>();
	}

	public Prompter (BufferedReader reader) {
		this(reader, null);
	}

	public void pushPrefix (String pfx) { this.prefixes.push(pfx); }
	public void popPrefix () { this.prefixes.pop(); }

	@FunctionalInterface interface PromptLineCallback { boolean valid (String line); }
	@FunctionalInterface interface PromptLongCallback { boolean valid (Long n); }
	@FunctionalInterface interface PromptDoubleCallback { boolean valid (Double d); }
	@FunctionalInterface interface PromptEnumCallback<E extends Enum> { boolean valid (E en); }

	public String nextLine (String prompt) throws IOException, PrompterInputAbortedException
	{
		return this.nextLine(prompt, (String line) -> !line.isEmpty());
	}

	public String nextLine (String prompt, PromptLineCallback cb) throws IOException, PrompterInputAbortedException
	{
		String line;

		if (this.writer == null) {
			// non-interactive mode
			line = this.reader.readLine();
			return cb.valid(line) ? line : null;
		} else {
			// interactive mode
			do {
				for (String prefix: this.prefixes) {
					this.writer.write(prefix);
				}
				this.writer.write(prompt);
				this.writer.flush();
				line = this.reader.readLine();
				if (line == null) {
					throw new PrompterInputAbortedException();
				}
				line.trim();
			} while (!cb.valid(line));
			return line;
		}
	}

	public Long nextLong (String prompt) throws IOException, PrompterInputAbortedException
	{
		return this.nextLong(prompt, l -> true);
	}

	public Long nextLong (String prompt, PromptLongCallback cb) throws IOException, PrompterInputAbortedException
	{
		var result = new Object() { public Long l; };
		String line = this.nextLine(prompt, (String l) -> {
			try {
				return cb.valid(result.l = Long.parseLong(l));
			} catch(NumberFormatException e) {
				return false;
			}
		});
		if (line == null) {
			return null;
		}
		return result.l;
	}

	public Double nextDouble (String prompt) throws IOException, PrompterInputAbortedException
	{
		return this.nextDouble(prompt, l -> true);
	}

	public Double nextDouble (String prompt, PromptDoubleCallback cb) throws IOException, PrompterInputAbortedException
	{
		var result = new Object() { public Double d; };
		String line = this.nextLine(prompt, (String l) -> {
			try {
				return cb.valid(result.d = Double.parseDouble(l));
			} catch(NumberFormatException e) {
				return false;
			}
		});
		if (line == null) {
			return null;
		}
		return result.d;
	}

	public <E extends Enum<E>> E nextEnum (String prompt, Class<E> enumClass, boolean nullOnEmpty) throws IOException, PrompterInputAbortedException
	{
		return this.nextEnum(prompt, enumClass, nullOnEmpty, e -> true);
	}

	public <E extends Enum<E>> E nextEnum (String prompt, Class<E> enumClass, boolean nullOnEmpty, PromptEnumCallback<E> cb) throws IOException, PrompterInputAbortedException
	{
		var result = new Object() { public E e; };
		String line = this.nextLine(prompt, (String l) -> {
			try {
				if (nullOnEmpty && l.isEmpty()) {
					return true;
				}
				return cb.valid(result.e = Enum.valueOf(enumClass, l.toUpperCase()));
			} catch (IllegalArgumentException e) {
				return false;
			}
		});
		if (line == null || line.isEmpty()) {
			return null;
		}
		return result.e;
	}

}