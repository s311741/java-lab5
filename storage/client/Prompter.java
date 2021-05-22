package storage.client;

import java.io.BufferedReader;
import java.io.Writer;
import java.io.IOException;
import java.util.Stack;

/**
 * The I/O device used to ask the user for information, or to read it from files. Consists
 * of a BufferedReader and a Writer, the latter of which may be null if we only read (e.g., from a file)
 * If there is a Writer, the Prompter assumes an interactive mode, and will ask again when
 * the input is incorrect. Otherwise, in automated mode, errors are less tolerated.
 */
public final class Prompter {
	private BufferedReader reader;
	private Writer writer;
	private Stack<String> prefixes;

	/**
	 * @param reader The reader that the prompter will use for information
	 * @param writer The writer to which the prompter will output prompts for the user
	 */
	public Prompter (BufferedReader reader, Writer writer) {
		this.reader = reader;
		this.writer = writer;
		this.prefixes = new Stack<String>();
	}

	/**
	 * The writer is null: non-interactive mode
	 * @param reader The reader that the prompter will use for information
	 */
	public Prompter (BufferedReader reader) {
		this(reader, null);
	}

	/**
	 * Push to the stack of prefixes that the Prompter will print before all its prompts.
	 */
	public void pushPrefix (String pfx) { this.prefixes.push(pfx); }

	/**
	 * Pop from the stack of prefixes that the Prompter will print before all its prompts.
	 */
	public void popPrefix () { this.prefixes.pop(); }

	@FunctionalInterface public interface PromptLineCallback { boolean valid (String line); }
	@FunctionalInterface public interface PromptLongCallback { boolean valid (Long n); }
	@FunctionalInterface public interface PromptDoubleCallback { boolean valid (Double d); }
	@FunctionalInterface public interface PromptEnumCallback<E extends Enum> { boolean valid (E en); }

	/**
	 * Get a non-empty line
	 * @param prompt The prompt message
	 */
	public String nextLine (String prompt) throws PrompterInputAbortedException
	{
		return this.nextLine(prompt, (String line) -> !line.isEmpty());
	}

	/**
	 * Get a line that is validated by the callback
	 * @param prompt The prompt message
	 * @param cb The (String -> boolean) callback to validate the input
	 */
	public String nextLine (String prompt, PromptLineCallback cb) throws PrompterInputAbortedException
	{
		String line;
		do {
			try {
				if (this.writer != null) {
					for (String prefix: this.prefixes) {
						this.writer.write(prefix);
					}
					this.writer.write(prompt);
					this.writer.flush();
				}
				line = this.reader.readLine();
			} catch (IOException e) {
				return null;
			}
			if (line == null) {
				throw new PrompterInputAbortedException();
			}
			line = line.trim();
		} while (!cb.valid(line));
		return line;
	}

	/**
	 * Get any Long
	 * @param prompt The prompt message
	 */
	public Long nextLong (String prompt) throws PrompterInputAbortedException
	{
		return this.nextLong(prompt, l -> true);
	}

	/**
	 * Get a Long that is validated by the callback
	 * @param prompt The prompt message
	 * @param cb The (Long -> boolean) callback to validate the input
	 */
	public Long nextLong (String prompt, PromptLongCallback cb) throws PrompterInputAbortedException
	{
		class WrapResult { public Long l; }
		WrapResult result = new WrapResult();
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

	/**
	 * Get any Double
	 * @param prompt The prompt message
	 */
	public Double nextDouble (String prompt) throws PrompterInputAbortedException
	{
		return this.nextDouble(prompt, l -> true);
	}

	/**
	 * Get a Double that is validated by the callback
	 * @param prompt The prompt message
	 * @param cb The (Double -> boolean) callback to validate the input
	 */
	public Double nextDouble (String prompt, PromptDoubleCallback cb) throws PrompterInputAbortedException
	{
		class WrapResult { public Double d; }
		WrapResult result = new WrapResult();
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

	/**
	 * Get a value of a Enum, or null if the line was empty (if allowed)
	 * @param prompt The prompt message
	 * @param enumClass The class of the enum in question
	 * @param nullOnEmpty whether to treat an empty line of input as valid and return null,
	 *                    or discard it as invalid
	 */
	public <E extends Enum<E>> E nextEnum (String prompt, Class<E> enumClass, boolean nullOnEmpty) throws PrompterInputAbortedException
	{
		return this.nextEnum(prompt, enumClass, nullOnEmpty, e -> true);
	}

	/**
	 * Get a value of a Enum, or null if the line was empty (if allowed), validated by a callback
	 * @param prompt The prompt message
	 * @param enumClass The class of the enum in question
	 * @param nullOnEmpty whether to treat an empty line of input as valid and return null,
	 *                    or discard it as invalid
	 * @param cb The callback to further validate a enum value
	 */
	public <E extends Enum<E>> E nextEnum (String prompt, Class<E> enumClass, boolean nullOnEmpty, PromptEnumCallback<E> cb) throws PrompterInputAbortedException
	{
		StringBuilder newPrompt = new StringBuilder(prompt + "(");
		for (E enumConstant: enumClass.getEnumConstants()) {
			newPrompt.append(enumConstant.toString().toLowerCase());
			newPrompt.append('/');
		}
		newPrompt.setLength(newPrompt.length()-1); // remove last slash
		newPrompt.append(") ");

		class WrapResult { public E e; }
		WrapResult result = new WrapResult();
		String line = this.nextLine(newPrompt.toString(), (String l) -> {
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