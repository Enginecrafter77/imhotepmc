package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** @deprecated Use {@link TesselatorRenderable} */
@Deprecated
@SideOnly(Side.CLIENT)
public interface IBufferRenderable {
	public void render(BufferBuilder buffer, float partialTicks);
}
