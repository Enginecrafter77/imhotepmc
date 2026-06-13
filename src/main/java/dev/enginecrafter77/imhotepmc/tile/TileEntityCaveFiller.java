package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.util.GraphBlockIterator;
import dev.enginecrafter77.imhotepmc.util.RelativeBlockPosList;
import dev.enginecrafter77.imhotepmc.util.TickModulator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class TileEntityCaveFiller extends TileEntity implements ITickable {
	private final RelativeBlockPosList caveModel;
	private final TickModulator scanModulator;
	private final TickModulator fillModulator;
	private int fillIndex;
	private boolean active;
	private State state;

	@Nullable
	private GraphBlockIterator scanningIterator;

	@Nullable
	private IBlockState fillBlock;

	public TileEntityCaveFiller()
	{
		this.caveModel = new RelativeBlockPosList(BlockPos.ORIGIN);
		this.scanModulator = new TickModulator(this::scanNextBlock);
		this.fillModulator = new TickModulator(this::fillOneBlock);
		this.state = State.IDLE;
		this.fillBlock = null;
		this.active = true;

		this.scanModulator.setTickRate(64.0F);
		this.fillModulator.setTickRate(32.0F);
	}

	public State getState()
	{
		return this.state;
	}

	public RelativeBlockPosList getCaveModel()
	{
		return this.caveModel;
	}

	public int getFilledBlocks()
	{
		return this.fillIndex;
	}

	public void setFillBlock(IBlockState fillBlock)
	{
		this.fillBlock = fillBlock;
	}

	public void scan()
	{
		if(this.state != State.IDLE)
			return;
		this.caveModel.reset(this.getScanOrigin());
		this.scanningIterator = GraphBlockIterator.bfs()
				.startingAt(this.getScanOrigin())
				.filter(this::canScanBlock)
				.build();
		this.state = State.SCANNING;
	}

	public void fill()
	{
		if(this.state != State.IDLE)
			return;
		this.fillIndex = 0;
		this.state = State.FILLING;
	}

	public void stop()
	{
		this.state = State.IDLE;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean isActive()
	{
		return this.active;
	}

	public BlockPos getScanOrigin()
	{
		return this.getPos().down();
	}

	private boolean canScanBlock(BlockPos pos)
	{
		if(pos.getY() > this.getScanOrigin().getY())
			return false;
		if(!this.caveModel.canAdd(pos))
			return false;
		if(this.world.isAirBlock(pos))
			return true;
		IBlockState blk = this.world.getBlockState(pos);
		return blk.getBlock().isReplaceable(this.world, pos);
	}

	private void scanNextBlock()
	{
		if(this.state != State.SCANNING)
			return;
		assert this.scanningIterator != null;
		if(!this.scanningIterator.hasNext())
		{
			this.state = State.IDLE;
			return;
		}

		BlockPos next = this.scanningIterator.next();
		this.caveModel.add(next);
	}

	private int getFillMappedIndex()
	{
		return (this.caveModel.size() - 1) - this.fillIndex;
	}

	private void fillOneBlock()
	{
		if(this.state != State.FILLING)
			return;

		if(this.fillBlock == null || this.fillIndex >= this.caveModel.size())
		{
			this.state = State.IDLE;
			return;
		}

		BlockPos pos = this.caveModel.get(this.getFillMappedIndex());
		this.world.setBlockState(pos, this.fillBlock);
		++this.fillIndex;
	}

	@Override
	public void update()
	{
		if(!this.active)
			return;
		switch(this.state)
		{
		case IDLE:
			return;
		case SCANNING:
			this.scanModulator.update();
			break;
		case FILLING:
			this.fillModulator.update();
			break;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.active = compound.getBoolean("active");
		this.caveModel.reset(this.getScanOrigin());
		this.caveModel.deserializeNBT((NBTTagIntArray)compound.getTag("cave_model"));
		this.fillIndex = compound.getInteger("fill_index");
		this.state = State.values()[compound.getByte("state")];
		if(compound.hasKey("block"))
		{
			this.fillBlock = NBTUtil.readBlockState(compound.getCompoundTag("block"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setBoolean("active", this.active);
		compound.setByte("state", (byte)this.state.saved().ordinal());
		compound.setTag("cave_model", this.caveModel.serializeNBT());
		compound.setInteger("fill_index", this.fillIndex);
		if(this.fillBlock != null)
			compound.setTag("block", NBTUtil.writeBlockState(new NBTTagCompound(), this.fillBlock));
		return super.writeToNBT(compound);
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.serializeNBT();
	}

	public static enum State {
		IDLE,
		SCANNING,
		FILLING;

		public State saved()
		{
			if(this == SCANNING)
				return IDLE;
			return this;
		}
	}
}
