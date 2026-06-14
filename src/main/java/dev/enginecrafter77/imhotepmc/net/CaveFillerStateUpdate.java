package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.tile.TileEntityCaveFiller;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CaveFillerStateUpdate implements IMessage {
	private TileEntityCaveFiller.State state;
	private TileEntityCaveFiller.MachineError error;
	private BlockPos pos;

	public CaveFillerStateUpdate(BlockPos pos, TileEntityCaveFiller.State state, TileEntityCaveFiller.MachineError error)
	{
		this.state = state;
		this.error = error;
		this.pos = pos;
	}

	public CaveFillerStateUpdate()
	{
		this.state = TileEntityCaveFiller.State.SCANNING;
		this.error = TileEntityCaveFiller.MachineError.OK;
		this.pos = BlockPos.ORIGIN;
	}

	public TileEntityCaveFiller.MachineError getError()
	{
		return this.error;
	}

	public TileEntityCaveFiller.State getState()
	{
		return this.state;
	}

	public BlockPos getTileEntityPosition()
	{
		return this.pos;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.pos = BlockPosUtil.readFromByteBuf(buf);
		this.state = TileEntityCaveFiller.State.values()[buf.readByte()];
		this.error = TileEntityCaveFiller.MachineError.values()[buf.readByte()];
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		BlockPosUtil.writeToByteBuf(buf, this.pos);
		buf.writeByte(this.state.ordinal());
		buf.writeByte(this.error.ordinal());
	}
}
