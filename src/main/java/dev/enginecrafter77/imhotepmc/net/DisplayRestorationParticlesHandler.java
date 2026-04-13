package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.util.VecUtil;
import dev.enginecrafter77.imhotepmc.util.math.Box3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import javax.vecmath.Point3d;
import java.util.Random;

public class DisplayRestorationParticlesHandler implements IMessageHandler<DisplayRestorationParticlesMessage, IMessage> {
	private final Point3d particle;
	private final Point3d center;
	private final Box3d box;

	public DisplayRestorationParticlesHandler()
	{
		this.particle = new Point3d();
		this.center = new Point3d();
		this.box = new Box3d();
	}

	@Nullable
	@Override
	public IMessage onMessage(DisplayRestorationParticlesMessage message, MessageContext ctx)
	{
		this.box.set(message.getBox());
		VecUtil.boxCenter(this.box, this.center);

		WorldClient worldClient = Minecraft.getMinecraft().world;
		for(int index = 0; index < message.getCount(); ++index)
		{
			randomPointIn(worldClient.rand, this.box, this.particle);
			worldClient.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.particle.x, this.particle.y, this.particle.y, 0D, 0.1D, 0D);
		}
		worldClient.playSound(this.center.x, this.center.y, this.center.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1F, 1F, false);
		return null;
	}

	private double randomDoubleIn(Random rng, double from, double to)
	{
		return from + rng.nextDouble() * (to - from);
	}

	private void randomPointIn(Random rng, Box3d box, Point3d point)
	{
		point.x = randomDoubleIn(rng, box.start.x, box.end.x);
		point.y = randomDoubleIn(rng, box.start.y, box.end.y);
		point.z = randomDoubleIn(rng, box.start.z, box.end.z);
	}
}
