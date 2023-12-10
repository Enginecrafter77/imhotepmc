package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class BuilderDwellUpdate implements IMessage {
	@Nonnull
	private BlockPos builderPos;

	@Nullable
	private ResourceLocation missingBlock;

	private long dwelling;

	public BuilderDwellUpdate(BlockPos builderPos, @Nullable Block missingBlock, long ticks)
	{
		this.builderPos = builderPos;
		this.missingBlock = Optional.ofNullable(missingBlock).map(Block::getRegistryName).orElse(null);
		this.dwelling = ticks;
	}

	public BuilderDwellUpdate()
	{
		this.builderPos = BlockPos.ORIGIN;
		this.missingBlock = null;
		this.dwelling = 0;
	}

	public BlockPos getBuilderPos()
	{
		return this.builderPos;
	}

	@Nullable
	public Block getMissingBlock()
	{
		if(this.missingBlock == null)
			return null;
		return Block.REGISTRY.getObject(this.missingBlock);
	}

	public long getDwellingTicks()
	{
		return this.dwelling;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.builderPos = BlockPosUtil.readFromByteBuf(buf);
		this.dwelling = buf.readLong();
		boolean present = buf.readBoolean();
		if(present)
			this.missingBlock = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		BlockPosUtil.writeToByteBuf(buf, this.builderPos);
		buf.writeLong(this.dwelling);
		if(this.missingBlock == null)
		{
			buf.writeBoolean(false);
		}
		else
		{
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, this.missingBlock.toString());
		}
	}
}
