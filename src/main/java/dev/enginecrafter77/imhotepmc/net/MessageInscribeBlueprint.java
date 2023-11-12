package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.NBTBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageInscribeBlueprint implements IMessage {
	private static final String NBT_KEY_POS = "position";
	private static final String NBT_KEY_SCHEM = "schematic";

	private final NBTBlueprintSerializer serializer;

	private final BlockPos.MutableBlockPos blockPos;
	private SchematicBlueprint blueprint;

	public MessageInscribeBlueprint()
	{
		this.blockPos = new BlockPos.MutableBlockPos();
		this.blueprint = null;
		this.serializer = new LitematicaBlueprintSerializer();
	}

	public BlockPos getPosition()
	{
		return this.blockPos;
	}

	public SchematicBlueprint getBlueprint()
	{
		return this.blueprint;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		if(tag == null)
			throw new IllegalStateException();
		NBTTagCompound pos = tag.getCompoundTag(NBT_KEY_POS);
		NBTTagCompound schem = tag.getCompoundTag(NBT_KEY_SCHEM);
		this.blueprint = this.serializer.deserializeBlueprint(schem);
		this.blockPos.setPos(NBTUtil.getPosFromTag(pos));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		NBTTagCompound pos = NBTUtil.createPosTag(this.blockPos);
		NBTTagCompound schem = this.serializer.serializeBlueprint(this.blueprint);

		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(NBT_KEY_POS, pos);
		tag.setTag(NBT_KEY_SCHEM, schem);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static MessageInscribeBlueprint createMessage(BlockPos pos, SchematicBlueprint blueprint)
	{
		MessageInscribeBlueprint msg = new MessageInscribeBlueprint();
		msg.blockPos.setPos(pos);
		msg.blueprint = blueprint;
		return msg;
	}
}
