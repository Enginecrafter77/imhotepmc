package dev.enginecrafter77.imhotepmc.radar;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.util.FastBlockPosSet;
import dev.enginecrafter77.imhotepmc.util.GraphBlockIterator;
import dev.enginecrafter77.imhotepmc.util.TickModulator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RadarEchoGroup {
	private final FastBlockPosSet pingTraversedBlocks;
	private final TickModulator tickModulator;
	private final World world;
	private final BlockPos origin;

	@Nullable
	private GraphBlockIterator iterator;

	private long lastPingTime;
	private boolean dirty;

	public RadarEchoGroup(World world, BlockPos origin)
	{
		this.world = world;
		this.tickModulator = new TickModulator(this::scanOneBlock);
		this.pingTraversedBlocks = new FastBlockPosSet();
		this.origin = origin;
		this.iterator = null;
		this.lastPingTime = world.getTotalWorldTime();
		this.dirty = true;

		this.tickModulator.setTickRate(64.0F);
	}

	public FastBlockPosSet getPingTraversedBlocks()
	{
		return this.pingTraversedBlocks;
	}

	public BlockPos getPingOrigin()
	{
		return this.origin;
	}

	public void markDirty()
	{
		this.dirty = true;
	}

	public long getTicksSinceLastPing()
	{
		return this.world.getTotalWorldTime() - this.lastPingTime;
	}

	private boolean canScanBlock(BlockPos pos)
	{
		if(pos.getY() <= 0)
			return false;
		if(!this.pingTraversedBlocks.relativeTo(this.origin).canAdd(pos))
			return false;
		IBlockState state = this.world.getBlockState(pos);
		return state.getBlock().isReplaceable(this.world, pos);
	}

	private void scanOneBlock()
	{
		if(this.iterator == null)
			this.iterator = GraphBlockIterator.bfs().startingAt(this.origin).filter(this::canScanBlock).build();

		if(!this.iterator.hasNext())
			return;
		this.pingTraversedBlocks.relativeTo(this.origin).add(this.iterator.next());
		this.markDirty();
	}

	public void ping()
	{
		this.tickModulator.update();
		this.lastPingTime = this.world.getTotalWorldTime();
	}

	public void dispatchUpdatePacket()
	{
		if(!this.dirty)
			return;
		RadarEchoUpdateMessage msg = RadarEchoUpdateMessage.update(this.origin, this.pingTraversedBlocks);
		ImhotepMod.instance.getNetChannel().sendToAll(msg);
		this.dirty = false;
	}
}
