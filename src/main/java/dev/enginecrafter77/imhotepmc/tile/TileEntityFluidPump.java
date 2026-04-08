package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.util.GraphBlockIterator;
import dev.enginecrafter77.imhotepmc.util.transaction.EnergyConsumeTransaction;
import dev.enginecrafter77.imhotepmc.util.transaction.FluidTransferTransaction;
import dev.enginecrafter77.imhotepmc.util.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;

import javax.annotation.Nullable;

public class TileEntityFluidPump extends TileEntity implements ITickable {
	private static final float PROGRESS_INCREMENT_PER_TICK = 0.01F;

	private final EnergyStorage battery;
	private final FluidTank fluidBuffer;

	@Nullable
	private GraphBlockIterator pumpIterator;
	private int pumpDepthTarget;
	private float pipeExtension;
	private boolean done;

	public TileEntityFluidPump()
	{
		this.battery = new EnergyStorage(64000);
		this.fluidBuffer = new FluidTank(10 * Fluid.BUCKET_VOLUME);
		this.pumpIterator = null;
		this.pumpDepthTarget = 0;
		this.pipeExtension = 0F;
		this.done = false;
	}

	public float getPipeExtension()
	{
		return this.pipeExtension;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY)
			return CapabilityEnergy.ENERGY.cast(this.battery);
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.fluidBuffer);
		return super.getCapability(capability, facing);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		serializeCapability(CapabilityEnergy.ENERGY, this.battery, null, compound, "battery");
		serializeCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this.fluidBuffer, null, compound, "tank");
		compound.setInteger("depth", this.pumpDepthTarget);
		compound.setBoolean("done", this.done);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		deserializeCapability(CapabilityEnergy.ENERGY, this.battery, null, compound, "battery");
		deserializeCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this.fluidBuffer, null, compound, "tank");
		this.pipeExtension = this.pumpDepthTarget = compound.getInteger("depth");
		this.done = compound.getBoolean("done");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return super.getRenderBoundingBox().expand(0D, -this.pipeExtension, 0D);
	}

	@Nullable
	private IFluidHandler getFluidHandlerForBlock(BlockPos pos)
	{
		IBlockState state = this.world.getBlockState(pos);
		Block block = state.getBlock();
		if(block instanceof IFluidBlock)
			return new FluidBlockWrapper((IFluidBlock)block, this.world, pos);
		else if(block instanceof BlockLiquid)
			return new BlockLiquidWrapper((BlockLiquid)block, this.world, pos);
		else
			return null;
	}

	private boolean canScanBlock(BlockPos pos)
	{
		if(maxCartesianDistance(pos) > 256)
			return false;
		if(this.world.isAirBlock(pos))
			return true;
		return this.getFluidHandlerForBlock(pos) != null;
	}

	private boolean canPumpBlock(BlockPos pos)
	{
		IFluidHandler handler = this.getFluidHandlerForBlock(pos);
		if(handler == null)
			return false;
		FluidStack currentTankContents = this.fluidBuffer.getFluid();
		if(currentTankContents == null)
			return true;
		FluidStack pumpContents = handler.drain(Fluid.BUCKET_VOLUME, false);
		if(pumpContents == null)
			return false;
		return pumpContents.getFluid() == currentTankContents.getFluid();
	}

	private BlockPos getPipePos(int depth)
	{
		return this.getPos().down(depth);
	}

	private GraphBlockIterator initIterator()
	{
		return GraphBlockIterator.bfs()
				.by(GraphBlockIterator.BlockExpandFunction.HPLANE.filter(this::canScanBlock))
				.startingAt(this.getPipePos(this.pumpDepthTarget))
				.build();
	}

	private boolean shouldLowerPipe()
	{
		if(this.done)
			return false;
		if(this.pumpDepthTarget == 0)
			return true;
		if(this.pumpIterator == null)
			return false;
		return !this.pumpIterator.hasNext();
	}

	private void tryLowerPipe()
	{
		BlockPos pipePos = this.getPipePos(this.pumpDepthTarget + 1);
		if(!this.canScanBlock(pipePos))
		{
			this.done = true; // we hit the bottom
			this.pumpIterator = null; // allow the iterator to be GC'd
			return;
		}

		this.pumpIterator = null;
		++this.pumpDepthTarget;
	}

	private void tryPushFluid()
	{
		TileEntity tileAbove = this.world.getTileEntity(this.getPos().up());
		if(tileAbove == null)
			return;
		IFluidHandler handler = tileAbove.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
		if(handler == null)
			return;
		(new FluidTransferTransaction(this.fluidBuffer, handler, 10000, true)).tryCommit();
	}

	@Override
	public void update()
	{
		this.tryPushFluid();
		if(this.pipeExtension < (float)this.pumpDepthTarget)
		{
			this.pipeExtension += PROGRESS_INCREMENT_PER_TICK;
			return; // cannot pump until pipe is fully extended
		}

		if(this.done)
			return;

		if(this.shouldLowerPipe())
		{
			this.tryLowerPipe();
			return; // don't do anything after starting the process of lowering the pipe (we need to wait util it is fully extended).
		}

		if(this.pumpIterator == null)
			this.pumpIterator = this.initIterator();

		BlockPos pos = this.pumpIterator.peek();
		while(pos != null && !this.canPumpBlock(pos))
		{
			this.pumpIterator.next();
			pos = this.pumpIterator.peek();
		}
		if(pos == null)
			return;

		IFluidHandler pumpedBlockHandler = this.getFluidHandlerForBlock(pos);
		EnergyConsumeTransaction consumeTransaction = new EnergyConsumeTransaction(this.battery, 1000);
		FluidTransferTransaction transferTransaction = new FluidTransferTransaction(pumpedBlockHandler, this.fluidBuffer, Fluid.BUCKET_VOLUME, false);

		if(Transaction.compose(consumeTransaction, transferTransaction).tryCommit())
		{
			this.pumpIterator.next();
		}
	}

	private int maxCartesianDistance(BlockPos other)
	{
		return Math.max(Math.abs(this.pos.getX() - other.getX()), Math.max(Math.abs(this.pos.getY() - other.getY()), Math.abs(this.pos.getZ() - other.getZ())));
	}

	private static <T> void serializeCapability(Capability<T> capability, T instance, @Nullable EnumFacing side, NBTTagCompound tag, String key)
	{
		NBTBase serializedTag = capability.writeNBT(instance, side);
		if(serializedTag == null)
			return;
		tag.setTag(key, serializedTag);
	}

	private static <T> void deserializeCapability(Capability<T> capability, T instance, @Nullable EnumFacing side, NBTTagCompound tag, String key)
	{
		if(!tag.hasKey(key))
			return;
		NBTBase serializedTag = tag.getTag(key);
		capability.readNBT(instance, side, serializedTag);
	}
}
