package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.util.Box3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import javax.vecmath.Point3d;
import java.util.Random;

public class DisplayRestorationParticlesHandler implements IMessageHandler<DisplayRestorationParticlesMessage, IMessage> {
	@Nullable
	@Override
	public IMessage onMessage(DisplayRestorationParticlesMessage message, MessageContext ctx)
	{
		WorldClient worldClient = Minecraft.getMinecraft().world;
		BlockPos min = message.getBox().getMinCorner();
		BlockPos max = message.getBox().getMaxCorner();
		for(int index = 0; index < message.getCount(); ++index)
		{
			double px = randomDoubleIn(worldClient.rand, min.getX(), max.getX());
			double py = randomDoubleIn(worldClient.rand, min.getY(), max.getY());
			double pz = randomDoubleIn(worldClient.rand, min.getZ(), max.getZ());
			worldClient.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, px, py, pz, 0D, 0.1D, 0D);
		}

		Point3d center = new Point3d();
		Box3d box = new Box3d();
		box.set(message.getBox());
		box.getCenter(center);
		worldClient.playSound(center.x, center.y, center.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1F, 1F, false);
		return null;
	}

	private double randomDoubleIn(Random rng, double from, double to)
	{
		return from + rng.nextDouble() * (to - from);
	}
}
