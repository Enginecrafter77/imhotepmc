package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.ImhotepConfig;
import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.net.CaveFillerStateUpdate;
import dev.enginecrafter77.imhotepmc.util.GraphBlockIterator;
import dev.enginecrafter77.imhotepmc.util.RelativeBlockPosList;
import dev.enginecrafter77.imhotepmc.util.TickModulator;
import dev.enginecrafter77.imhotepmc.util.transaction.EnergyConsumeTransaction;
import dev.enginecrafter77.imhotepmc.util.transaction.MatchingItemExtractTransaction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class TileEntityCaveFiller extends TileEntity implements ITickable {
	private final RelativeBlockPosList caveModel;
	private final TickModulator scanModulator;
	private final TickModulator fillModulator;
	private final EnergyStorage battery;
	private int fillIndex;
	private State state;
	private State remoteState;
	private MachineError error;
	private MachineError remoteError;

	@Nullable
	private GraphBlockIterator scanningIterator;

	private final MatchingItemExtractTransaction itemFetchTransaction;
	private final EnergyConsumeTransaction scanEnergyConsume;
	private final EnergyConsumeTransaction fillEnergyConsume;

	public TileEntityCaveFiller()
	{
		this.battery = new EnergyStorage(16000, 1000, 1000);
		this.caveModel = new RelativeBlockPosList(BlockPos.ORIGIN);
		this.scanModulator = new TickModulator(this::scanNextBlock);
		this.fillModulator = new TickModulator(this::fillOneBlock);
		this.itemFetchTransaction = new MatchingItemExtractTransaction();
		this.scanEnergyConsume = new EnergyConsumeTransaction(this.battery, ImhotepConfig.energy.caveFillerScanCost);
		this.fillEnergyConsume = new EnergyConsumeTransaction(this.battery, ImhotepConfig.energy.caveFillerFillCost);

		this.itemFetchTransaction.setFilter(TileEntityCaveFiller::canFillUsing);
		this.itemFetchTransaction.setExtractAmount(1);
		this.scanModulator.setTickRate(64.0F);
		this.fillModulator.setTickRate(32.0F);

		this.reset();
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

	public boolean isActive()
	{
		return !this.world.isBlockPowered(this.getPos());
	}

	public BlockPos getScanOrigin()
	{
		return this.getPos().down();
	}

	public MachineError getError()
	{
		return this.error;
	}

	public void reset()
	{
		this.state = State.SCANNING;
		this.remoteState = State.SCANNING;
		this.scanningIterator = null;
		this.fillIndex = 0;
		this.error = MachineError.OK;
		this.remoteError = MachineError.OK;
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
		if(!this.scanningIterator.hasNext() || this.caveModel.size() >= ImhotepConfig.general.caveFillerMaxBlocks)
		{
			this.state = State.FILLING;
			return;
		}

		if(!this.scanEnergyConsume.tryCommit())
		{
			this.error = MachineError.INSUFFICIENT_POWER;
			return;
		}

		BlockPos next = this.scanningIterator.next();
		this.caveModel.add(next);
	}

	private int getFillMappedIndex()
	{
		return (this.caveModel.size() - 1) - this.fillIndex;
	}

	private static boolean canFillUsing(ItemStack stack)
	{
		if(!(stack.getItem() instanceof ItemBlock))
			return false;
		Block blk = ((ItemBlock)stack.getItem()).getBlock();
		return blk.getDefaultState().isNormalCube();
	}

	private void fillOneBlock()
	{
		if(this.state != State.FILLING)
			return;

		if(this.fillIndex >= this.caveModel.size())
		{
			this.state = State.DONE;
			return;
		}

		this.itemFetchTransaction.invalidate();
		if(!this.fillEnergyConsume.canCommit())
		{
			this.error = MachineError.INSUFFICIENT_POWER;
			return;
		}
		if(!this.itemFetchTransaction.canCommit())
		{
			this.error = MachineError.NO_AVAILABLE_BLOCK;
			return;
		}
		this.fillEnergyConsume.commit();
		this.itemFetchTransaction.commit();

		IBlockState fillBlock = ((ItemBlock)this.itemFetchTransaction.getExtractedItem().getItem()).getBlock().getDefaultState();
		BlockPos pos = this.caveModel.get(this.getFillMappedIndex());
		this.world.setBlockState(pos, fillBlock);
		++this.fillIndex;
	}

	private void updateSourceInventory()
	{
		TileEntity above = this.world.getTileEntity(this.getPos().up());
		if(above == null)
		{
			this.itemFetchTransaction.setSourceInventory(null);
			return;
		}

		IItemHandler handler = above.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		this.itemFetchTransaction.setSourceInventory(handler);
	}

	private void broadcastStateUpdate()
	{
		if(this.state == this.remoteState && this.error == this.remoteError)
			return;
		ImhotepMod.instance.getNetChannel().sendToAll(new CaveFillerStateUpdate(this.getPos(), this.state, this.error));
		this.remoteState = this.state;
		this.remoteError = this.error;
	}

	@SideOnly(Side.CLIENT)
	public void handleClientStateUpdate(CaveFillerStateUpdate update)
	{
		this.state = update.getState();
		this.error = update.getError();
	}

	@Override
	public void update()
	{
		if(!this.isActive() || this.world.isRemote) // we cannot do this on client since client's EnergyStorage is not being synced up
			return;

		this.error = MachineError.OK;
		switch(this.state)
		{
		case SCANNING:
			if(this.scanningIterator == null)
			{
				this.caveModel.reset(this.getScanOrigin());
				this.scanningIterator = GraphBlockIterator.bfs()
						.startingAt(this.getScanOrigin())
						.filter(this::canScanBlock)
						.build();
			}
			this.scanModulator.update();
			break;
		case FILLING:
			this.updateSourceInventory();
			this.fillModulator.update();
			break;
		case DONE:
			break;
		}
		this.broadcastStateUpdate();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.caveModel.reset(this.getScanOrigin());
		this.caveModel.deserializeNBT((NBTTagIntArray)compound.getTag("cave_model"));
		this.fillIndex = compound.getInteger("fill_index");
		this.state = State.values()[compound.getByte("state")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setByte("state", (byte)this.state.ordinal());
		compound.setTag("cave_model", this.caveModel.serializeNBT());
		compound.setInteger("fill_index", this.fillIndex);
		return super.writeToNBT(compound);
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		NBTTagCompound tag = super.getUpdateTag();
		tag.setByte("state", (byte)this.state.ordinal());
		tag.setByte("status", (byte)this.error.ordinal());
		return tag;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		this.state = State.values()[tag.getByte("state")];
		this.error = MachineError.values()[tag.getByte("status")];
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return super.hasCapability(capability, facing) || capability == CapabilityEnergy.ENERGY;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY)
			return CapabilityEnergy.ENERGY.cast(this.battery);
		return super.getCapability(capability, facing);
	}

	public static enum State {
		SCANNING,
		FILLING,
		DONE
	}

	public enum MachineError {
		OK,
		INSUFFICIENT_POWER,
		NO_AVAILABLE_BLOCK
	}
}
