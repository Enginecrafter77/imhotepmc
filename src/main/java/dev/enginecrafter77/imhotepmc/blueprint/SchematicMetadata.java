package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.Vec3i;

import java.time.Instant;

public interface SchematicMetadata {
	public String getName();
	public String getAuthor();
	public String getDescription();
	public Instant getCreateTime();
	public Instant getModifyTime();
	public Vec3i getSize();
	public int getRegionCount();
	public int getBlockCount();

	public boolean equals(Object other);

	public int hashCode();
}
