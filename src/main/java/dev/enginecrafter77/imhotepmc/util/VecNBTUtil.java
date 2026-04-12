package dev.enginecrafter77.imhotepmc.util;

import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import dev.enginecrafter77.imhotepmc.util.math.Box3d;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class VecNBTUtil {
	public static NBTBase serializeBox3iToNBT(Box3i box)
	{
		return new NBTTagIntArray(new int[] {box.start.x, box.start.y, box.start.z, box.end.x, box.end.y, box.end.z});
	}

	public static void deserializeBox3iFromNBT(NBTBase tag, Box3i box)
	{
		if(!(tag instanceof NBTTagIntArray))
			throw new IllegalArgumentException("Supplied tag is not NBTTagIntArray!");
		int[] array = ((NBTTagIntArray)tag).getIntArray();
		box.start.x = array[0];
		box.start.y = array[1];
		box.start.z = array[2];
		box.end.x = array[3];
		box.end.y = array[4];
		box.end.z = array[5];
	}

	public static NBTBase serializeBox3dToNBT(Box3d box)
	{
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagDouble(box.start.x));
		list.appendTag(new NBTTagDouble(box.start.y));
		list.appendTag(new NBTTagDouble(box.start.z));
		list.appendTag(new NBTTagDouble(box.end.x));
		list.appendTag(new NBTTagDouble(box.end.y));
		list.appendTag(new NBTTagDouble(box.end.z));
		return list;
	}

	public static void deserializeBox3dFromNBT(NBTBase tag, Box3d box)
	{
		if(!(tag instanceof NBTTagList))
			throw new IllegalArgumentException("Supplied tag is not NBTTagList!");
		NBTTagList listTag = (NBTTagList)tag;
		if(listTag.getTagType() != Constants.NBT.TAG_DOUBLE)
			throw new IllegalArgumentException("Supplied tag is not NBTTagList[NBTTagDouble]!");
		box.start.x = listTag.getDoubleAt(0);
		box.start.y = listTag.getDoubleAt(1);
		box.start.z = listTag.getDoubleAt(2);
		box.end.x = listTag.getDoubleAt(3);
		box.end.y = listTag.getDoubleAt(4);
		box.end.z = listTag.getDoubleAt(5);
	}
}
