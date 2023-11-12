package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.Vec3i;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public class MutableSchematicMetadata implements SchematicMetadata {
	private Instant modifyTime;
	private Instant createTime;
	private String description;
	private String author;
	private String name;
	private Vec3i size;
	private int regionCount;
	private int blockCount;

	public MutableSchematicMetadata()
	{
		this.name = "Unnamed";
		this.author = "Unknown";
		this.description = "";
		this.createTime = Instant.EPOCH;
		this.modifyTime = Instant.EPOCH;
		this.size = Vec3i.NULL_VECTOR;
		this.regionCount = 0;
		this.blockCount = 0;
	}

	public void set(SchematicMetadata other)
	{
		this.name = other.getName();
		this.author = other.getAuthor();
		this.description = other.getDescription();
		this.createTime = other.getCreateTime();
		this.modifyTime = other.getModifyTime();
		this.blockCount = other.getBlockCount();
		this.size = other.getSize();
		this.regionCount = other.getRegionCount();
	}

	public MutableSchematicMetadata copy()
	{
		MutableSchematicMetadata copy = new MutableSchematicMetadata();
		copy.set(this);
		return copy;
	}

	@Override
	public String getAuthor()
	{
		return this.author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public Instant getCreateTime()
	{
		return this.createTime;
	}

	public void setCreateTime(Instant createTime)
	{
		this.createTime = createTime;
	}

	@Override
	public Instant getModifyTime()
	{
		return this.modifyTime;
	}

	public void setModifyTime(Instant instant)
	{
		this.modifyTime = instant;
	}

	@Override
	public Vec3i getSize()
	{
		return this.size;
	}

	public void setSize(Vec3i size)
	{
		this.size = size;
	}

	@Override
	public int getRegionCount()
	{
		return this.regionCount;
	}

	public void setRegionCount(int regionCount)
	{
		this.regionCount = regionCount;
	}

	@Override
	public int getBlockCount()
	{
		return this.blockCount;
	}

	public void setBlockCount(int blockCount)
	{
		this.blockCount = blockCount;
	}

	@Override
	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.createCompareArray());
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof MutableSchematicMetadata))
			return false;
		if(obj == this)
			return true;
		MutableSchematicMetadata other = (MutableSchematicMetadata)obj;
		Object[] lca = this.createCompareArray();
		Object[] oca = other.createCompareArray();
		return Arrays.equals(lca, oca);
	}

	private Object[] createCompareArray()
	{
		return new Object[] {this.name, this.author, this.description, this.size, this.createTime, this.modifyTime, this.regionCount, this.blockCount};
	}
}
