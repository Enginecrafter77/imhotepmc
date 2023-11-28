package dev.enginecrafter77.imhotepmc.world.sync;

import net.minecraft.world.storage.WorldSavedData;

public abstract class SynchronizedWorldSavedData extends WorldSavedData {
	private boolean needsSync;

	public SynchronizedWorldSavedData(String name)
	{
		super(name);
		this.needsSync = false;
	}

	@Override
	public void markDirty()
	{
		super.markDirty();
		this.needsSync = true;
	}

	public boolean doesNeedsSync()
	{
		return this.needsSync;
	}

	public void setNeedsSync(boolean needsSync)
	{
		this.needsSync = needsSync;
	}
}
