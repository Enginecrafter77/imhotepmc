package dev.enginecrafter77.imhotepmc.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NBTPath {
	private final List<NBTPathDescent> descents;

	public NBTPath(List<NBTPathDescent> descents)
	{
		this.descents = descents;
	}

	public int length()
	{
		return this.descents.size();
	}

	public NBTPath sub(String key)
	{
		return this.deriveNext(new NBTPathCompoundSelector(key));
	}

	public NBTPath at(int index)
	{
		return this.deriveNext(new NBTPathListSelector(index));
	}

	protected NBTPath deriveNext(NBTPathDescent descent)
	{
		ImmutableList.Builder<NBTPathDescent> builder = ImmutableList.builder();
		builder.addAll(this.descents);
		builder.add(descent);
		return new NBTPath(builder.build());
	}

	@Nullable
	public NBTBase apply(NBTBase nbt)
	{
		Iterator<NBTPathDescent> itr = this.descents.iterator();
		while(nbt != null && itr.hasNext())
		{
			NBTPathDescent descent = itr.next();
			nbt = descent.descend(nbt);
		}
		return nbt;
	}

	private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("([A-Za-z_0-9]*)\\[([0-9]+)]");

	private static void generateDescentForName(String name, ImmutableList.Builder<NBTPathDescent> descentBuilder)
	{
		Matcher matcher = ARRAY_INDEX_PATTERN.matcher(name);
		if(matcher.matches())
		{
			String compoundDescent = matcher.group(1);
			if(!compoundDescent.isEmpty())
				descentBuilder.add(new NBTPathCompoundSelector(matcher.group(1)));
			descentBuilder.add(new NBTPathListSelector(Integer.parseInt(matcher.group(2))));
		}
		else
		{
			descentBuilder.add(new NBTPathCompoundSelector(name));
		}
	}

	public static NBTPath compile(String path)
	{
		ImmutableList.Builder<NBTPathDescent> descentBuilder = ImmutableList.builder();
		while(!path.isEmpty())
		{
			if(path.charAt(0) != '/')
				throw new IllegalArgumentException();
			path = path.substring(1); // Remove leading slash

			if(path.isEmpty())
				break;

			int nextSlash = path.indexOf('/');
			if(nextSlash != -1)
			{
				String dir = path.substring(0, nextSlash);
				generateDescentForName(dir, descentBuilder);
				path = path.substring(nextSlash);
				continue;
			}
			generateDescentForName(path, descentBuilder);
			break;
		}
		return new NBTPath(descentBuilder.build());
	}

	public static interface NBTPathDescent
	{
		@Nullable
		public NBTBase descend(NBTBase parent);
	}

	public static class NBTPathCompoundSelector implements NBTPathDescent
	{
		private final String key;

		public NBTPathCompoundSelector(String key)
		{
			this.key = key;
		}

		@Nullable
		@Override
		public NBTBase descend(NBTBase parent)
		{
			return ((NBTTagCompound)parent).getTag(this.key);
		}
	}

	public static class NBTPathListSelector implements NBTPathDescent
	{
		private final int index;

		public NBTPathListSelector(int index)
		{
			this.index = index;
		}

		@Nullable
		@Override
		public NBTBase descend(NBTBase parent)
		{
			return ((NBTTagList)parent).get(this.index);
		}
	}
}
