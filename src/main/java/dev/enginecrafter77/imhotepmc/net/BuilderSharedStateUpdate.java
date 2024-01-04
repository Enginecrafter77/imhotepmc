package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.tile.SharedBuilderState;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class BuilderSharedStateUpdate implements IMessage {
	private final SharedBuilderState state;
	private BlockPos builderPos;

	public BuilderSharedStateUpdate(BlockPos builderPos, SharedBuilderState state)
	{
		this();
		this.builderPos = builderPos;
		this.state.set(state);
	}

	public BuilderSharedStateUpdate()
	{
		this.state = new SharedBuilderState();
		this.builderPos = BlockPos.ORIGIN;
	}

	public BlockPos getBuilderPos()
	{
		return this.builderPos;
	}

	public void exportState(SharedBuilderState state)
	{
		state.set(this.state);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.builderPos = BlockPosUtil.readFromByteBuf(buf);

		NBTTagCompound state = ByteBufUtils.readTag(buf);
		if(state == null)
			throw new IllegalStateException();
		this.state.deserializeNBT(state);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		BlockPosUtil.writeToByteBuf(buf, this.builderPos);
		ByteBufUtils.writeTag(buf, this.state.serializeNBT());
	}
}
