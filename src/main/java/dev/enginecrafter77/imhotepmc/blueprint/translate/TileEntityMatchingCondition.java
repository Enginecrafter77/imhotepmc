package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import dev.enginecrafter77.imhotepmc.util.NBTPath;
import net.minecraft.nbt.NBTBase;

import java.util.Objects;

public class TileEntityMatchingCondition implements RuleCondition {
	private final NBTPath path;
	private final String value;

	public TileEntityMatchingCondition(NBTPath path, String value)
	{
		this.path = path;
		this.value = value;
	}

	@Override
	public boolean evaluate(BlueprintEntry entry)
	{
		if(!entry.hasTileEntity())
			return false;
		NBTBase tag = this.path.apply(Objects.requireNonNull(entry.getTileEntitySavedData()));
		if(tag == null)
			return false;
		return this.value.equals(tag.toString());
	}

	@Override
	public int weight()
	{
		return 1000 + 10 * this.path.length();
	}
}
