package storage;

public final class CmdSumNumberOfRooms extends Cmd {
	public CmdSumNumberOfRooms (String[] arguments) { super(arguments); }

	@Override
	public boolean run () {
		long answer = 0;

		for (Flat flat: Storage.getStorage()) {
			answer += flat.getNumberOfRooms();
		}

		System.out.println(answer);
		return true;
	}
}
