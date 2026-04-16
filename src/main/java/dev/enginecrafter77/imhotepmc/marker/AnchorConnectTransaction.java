package dev.enginecrafter77.imhotepmc.marker;

import dev.enginecrafter77.imhotepmc.util.transaction.Transaction;

import javax.annotation.Nullable;

public class AnchorConnectTransaction implements Transaction {
	private final AreaMarkHandler handler;
	private final AreaMarkingActor actor;
	private final MarkingAnchor a1;
	private final MarkingAnchor a2;

	@Nullable
	private AreaExpandResult result;

	public AnchorConnectTransaction(AreaMarkHandler handler, AreaMarkingActor actor, MarkingAnchor a1, MarkingAnchor a2)
	{
		this.handler = handler;
		this.actor = actor;
		this.a1 = a1;
		this.a2 = a2;
		this.result = null;
	}

	/**
	 * Returns the result the connection operation as {@link AreaExpandResult} enumeration.
	 * The result will become available after running either {@link #canCommit()} (in which case
	 * the result will be a simulated one), OR after running {@link #commit()} (in which case
	 * the result is a real one). If neither operation was run before, the result will be <code>null</code>.
	 * @return A previously returned result of the {@link AreaMarkHandler#connect(AreaMarkingActor, MarkingAnchor, MarkingAnchor, boolean)} operation, or <code>null</code> if the result hasn't been examined yet.
	 */
	@Nullable
	public AreaExpandResult getOperationResult()
	{
		return this.result;
	}

	@Override
	public boolean canCommit()
	{
		this.result = this.handler.connect(this.actor, this.a1, this.a2, true);
		return this.result == AreaExpandResult.SUCCESS;
	}

	@Override
	public void commit()
	{
		this.result = this.handler.connect(this.actor, this.a1, this.a2, false);
	}
}
