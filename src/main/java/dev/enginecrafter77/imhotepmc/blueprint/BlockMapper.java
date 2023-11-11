package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface BlockMapper {
	@Nullable
	public ResourceLocation translate(ResourceLocation source);

	public static BlockMapper identity()
	{
		return (ResourceLocation res) -> res;
	}
}
