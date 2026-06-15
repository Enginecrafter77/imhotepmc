package dev.enginecrafter77.imhotepmc.radar;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.util.FastBlockPosSet;
import dev.enginecrafter77.imhotepmc.util.TickModulator;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class RadarTracker implements RadarHandler, ITickable {
	private final TickModulator purgeModulator;
	private final TickModulator syncModulator;
	private final Map<BlockPos, RadarEchoGroup> groups;
	private final World world;

	public RadarTracker(World world)
	{
		this.purgeModulator = new TickModulator(this::purge);
		this.syncModulator = new TickModulator(this::sync);
		this.groups = new TreeMap<BlockPos, RadarEchoGroup>();
		this.world = world;

		this.purgeModulator.setTickRate(1F/20F);
		this.syncModulator.setTickRate(1F/60F);
	}

	public Collection<RadarEchoGroup> groups()
	{
		return this.groups.values();
	}

	public RadarEchoGroup from(BlockPos pos)
	{
		return this.groups.computeIfAbsent(pos, k -> new RadarEchoGroup(this.world, k));
	}

	@Override
	public FastBlockPosSet ping(BlockPos pos)
	{
		RadarEchoGroup grp = this.from(pos);
		grp.ping();
		return grp.getPingTraversedBlocks();
	}

	public void purge()
	{
		Iterator<RadarEchoGroup> itr = this.groups.values().iterator();
		while(itr.hasNext())
		{
			RadarEchoGroup grp = itr.next();
			if(grp.getTicksSinceLastPing() < 20)
				continue;
			itr.remove();
			RadarEchoUpdateMessage msg = RadarEchoUpdateMessage.delete(grp.getPingOrigin());
			ImhotepMod.instance.getNetChannel().sendToAll(msg);
		}
	}

	public void sync()
	{
		this.groups.values().forEach(RadarEchoGroup::dispatchUpdatePacket);
	}

	@Override
	public void update()
	{
		this.purgeModulator.update();
		this.syncModulator.update();
	}
}
