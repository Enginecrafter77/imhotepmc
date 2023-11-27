package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.Vec3i;

import java.time.Instant;

public abstract class SchematicMetadataWrapper implements SchematicMetadata {
	protected abstract SchematicMetadata getWrappedMetadata();

	@Override
	public String getName()
	{
		return this.getWrappedMetadata().getName();
	}

	@Override
	public String getAuthor()
	{
		return this.getWrappedMetadata().getAuthor();
	}

	@Override
	public String getDescription()
	{
		return this.getWrappedMetadata().getDescription();
	}

	@Override
	public Instant getCreateTime()
	{
		return this.getWrappedMetadata().getCreateTime();
	}

	@Override
	public Instant getModifyTime()
	{
		return this.getWrappedMetadata().getModifyTime();
	}

	@Override
	public Vec3i getSize()
	{
		return this.getWrappedMetadata().getSize();
	}

	@Override
	public int getRegionCount()
	{
		return this.getWrappedMetadata().getRegionCount();
	}

	@Override
	public int getDefinedBlockCount()
	{
		return this.getWrappedMetadata().getDefinedBlockCount();
	}
}
