package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.util.NBTPath;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestNBTPath {
	@Test
	public void testSimpleTraversal()
	{
		NBTTagCompound root = new NBTTagCompound();
		root.setInteger("a", 42);

		NBTPath path = NBTPath.compile("/a");

		NBTBase res = path.apply(root);

		Assertions.assertNotNull(res);
		Assertions.assertInstanceOf(NBTTagInt.class, res);
		Assertions.assertEquals(42, ((NBTTagInt)res).getInt());
	}

	@Test
	public void testReferenceSelf()
	{
		NBTTagInt root = new NBTTagInt(42);

		NBTPath path = NBTPath.compile("/");

		NBTBase res = path.apply(root);

		Assertions.assertNotNull(res);
		Assertions.assertInstanceOf(NBTTagInt.class, res);
		Assertions.assertEquals(42, ((NBTTagInt)res).getInt());
	}

	@Test
	public void testArrayIndex()
	{
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagInt(19));
		list.appendTag(new NBTTagInt(42));
		list.appendTag(new NBTTagInt(23));

		NBTPath path = NBTPath.compile("/[1]");

		NBTBase res = path.apply(list);

		Assertions.assertNotNull(res);
		Assertions.assertInstanceOf(NBTTagInt.class, res);
		Assertions.assertEquals(42, ((NBTTagInt)res).getInt());
	}

	@Test
	public void testComplexDescent()
	{
		NBTTagCompound root = new NBTTagCompound();
			NBTTagCompound l1 = new NBTTagCompound();
				NBTTagList ll = new NBTTagList();
					NBTTagCompound l2 = new NBTTagCompound();
					l2.setInteger("v", 42);
				ll.appendTag(l2);
			l1.setTag("a", ll);
		root.setTag("c", l1);

		NBTPath path = NBTPath.compile("/c/a[0]/v");

		NBTBase res = path.apply(root);

		Assertions.assertNotNull(res);
		Assertions.assertInstanceOf(NBTTagInt.class, res);
		Assertions.assertEquals(42, ((NBTTagInt)res).getInt());
	}

	@Test
	public void testNonexistent()
	{
		NBTTagCompound root = new NBTTagCompound();
		root.setInteger("v", 42);

		NBTPath path = NBTPath.compile("/c");

		NBTBase res = path.apply(root);

		Assertions.assertNull(res);
	}
}
