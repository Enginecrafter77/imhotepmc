package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.util.TickModulator;
import net.minecraft.util.ITickable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class TickModulatorTest {
	private final Random rng;

	public TickModulatorTest()
	{
		this.rng = new Random();
	}

	@Test
	public void testSupermodulation2()
	{
		MockTickable tickable = new MockTickable();
		TickModulator modulator = new TickModulator(tickable);

		modulator.setTickRate(2F);
		modulator.update();

		Assertions.assertEquals(2, tickable.tickCount);
	}

	@Test
	public void testSupermodulationN()
	{
		MockTickable tickable = new MockTickable();
		TickModulator modulator = new TickModulator(tickable);

		int rate = this.rng.nextInt(32) + 2;

		modulator.setTickRate(rate);
		modulator.update();

		Assertions.assertEquals(rate, tickable.tickCount);
	}

	@Test
	public void testSubmodulation2()
	{
		MockTickable tickable = new MockTickable();
		TickModulator modulator = new TickModulator(tickable);

		modulator.setTickRate(0.5F);

		modulator.update();
		Assertions.assertEquals(0, tickable.tickCount);
		modulator.update();
		Assertions.assertEquals(1, tickable.tickCount);
		modulator.update();
		Assertions.assertEquals(1, tickable.tickCount);
		modulator.update();
		Assertions.assertEquals(2, tickable.tickCount);
	}

	@Test
	public void testSubmodulationN()
	{
		MockTickable tickable = new MockTickable();
		TickModulator modulator = new TickModulator(tickable);

		int rate = this.rng.nextInt(32) + 2;

		modulator.setTickRate(1.0F / rate);

		for(int i = 1; i < rate; ++i)
		{
			modulator.update();
			Assertions.assertEquals(0, tickable.tickCount);
		}
		modulator.update();
		Assertions.assertEquals(1, tickable.tickCount);
	}

	@Test
	public void testArbitraryModulation()
	{
		MockTickable tickable = new MockTickable();
		TickModulator modulator = new TickModulator(tickable);

		float rate = this.rng.nextFloat() * 10F;
		int upticks = this.rng.nextInt(256) + 1;

		modulator.setTickRate(rate);
		for(int i = 0; i < upticks; ++i)
			modulator.update();
		Assertions.assertEquals((int)Math.floor(upticks * rate), tickable.tickCount);
	}

	static class MockTickable implements ITickable
	{
		int tickCount;

		public MockTickable()
		{
			this.tickCount = 0;
		}

		@Override
		public void update()
		{
			++this.tickCount;
		}
	}
}
