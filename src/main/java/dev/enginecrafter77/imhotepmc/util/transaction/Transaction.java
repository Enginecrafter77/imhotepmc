package dev.enginecrafter77.imhotepmc.util.transaction;

import java.util.stream.Stream;

public interface Transaction {
	public boolean canCommit();
	public void commit();

	public default boolean tryCommit()
	{
		if(this.canCommit())
		{
			this.commit();
			return true;
		}
		return false;
	}

	public static Transaction compose(Transaction... parts)
	{
		return new Transaction() {
			@Override
			public boolean canCommit()
			{
				return Stream.of(parts).allMatch(Transaction::canCommit);
			}

			@Override
			public void commit()
			{
				Stream.of(parts).forEach(Transaction::commit);
			}
		};
	}
}
