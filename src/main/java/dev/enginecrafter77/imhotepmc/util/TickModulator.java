package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.ITickable;

public class TickModulator implements ITickable {
	private final ITickable delegate;
	private float tokensPerTick;
	private float currentTokens;

	public TickModulator(ITickable delegate)
	{
		this.tokensPerTick = 1F;
		this.currentTokens = 0F;
		this.delegate = delegate;
	}

	public void setTickRate(float rate)
	{
		this.tokensPerTick = rate;
	}

	@Override
	public void update()
	{
		this.currentTokens += this.tokensPerTick;
		while(this.currentTokens >= 1F)
		{
			this.currentTokens -= 1F;
			this.delegate.update();
		}
	}
}
