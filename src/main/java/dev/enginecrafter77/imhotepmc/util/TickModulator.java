package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.ITickable;

public class TickModulator implements ITickable {
	private static final int ONE_TICK_COST = 1 << 16;

	private final ITickable delegate;
	private int tokensPerTick;
	private int currentTokens;

	public TickModulator(ITickable delegate)
	{
		this.tokensPerTick = ONE_TICK_COST;
		this.currentTokens = 0;
		this.delegate = delegate;
	}

	public void setTickRate(float rate)
	{
		this.tokensPerTick = (int)(rate * ONE_TICK_COST);
	}

	@Override
	public void update()
	{
		this.currentTokens += this.tokensPerTick;
		while(this.currentTokens >= ONE_TICK_COST)
		{
			this.currentTokens -= ONE_TICK_COST;
			this.delegate.update();
		}
	}
}
