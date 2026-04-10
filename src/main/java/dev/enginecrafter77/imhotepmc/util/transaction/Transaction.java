package dev.enginecrafter77.imhotepmc.util.transaction;

import java.util.Collection;
import java.util.stream.Stream;

public interface Transaction {
	/** An always-committable noop-committing transaction */
	public static final Transaction PLACEHOLDER = Transaction.from(() -> {});

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

	public static Transaction compose(Collection<Transaction> parts)
	{
		return new Transaction() {
			@Override
			public boolean canCommit()
			{
				return parts.stream().allMatch(Transaction::canCommit);
			}

			@Override
			public void commit()
			{
				parts.forEach(Transaction::commit);
			}
		};
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

	public static Transaction from(Runnable action)
	{
		return new Transaction() {
			@Override
			public boolean canCommit()
			{
				return true;
			}

			@Override
			public void commit()
			{
				action.run();
			}
		};
	}
}
