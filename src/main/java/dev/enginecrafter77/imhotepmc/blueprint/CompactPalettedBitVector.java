package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableBiMap;
import dev.enginecrafter77.imhotepmc.util.LocalReflectionHelper;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

public class CompactPalettedBitVector<T> implements Iterable<T>, INBTSerializable<NBTTagLongArray> {
	private static final Field NLA_DATA_FIELD = LocalReflectionHelper.findField(NBTTagLongArray.class, "data", "field_193587_b");

	private final ImmutableBiMap<T, Integer> palette;

	private final int bitsPerEntry;
	private final int entriesPerSlot;
	private final long entryMask;

	private int entryCount;
	private long[] data;

	protected CompactPalettedBitVector(ImmutableBiMap<T, Integer> palette)
	{
		this.palette = palette;
		this.bitsPerEntry = Math.max((int)Math.ceil(Math.log(palette.size()) / Math.log(2)), 1);
		this.entriesPerSlot = (Long.BYTES * 8) / this.bitsPerEntry;
		this.entryMask = ~(-1L << this.bitsPerEntry);
	}

	protected CompactPalettedBitVector(List<T> palette)
	{
		this(CompactPalettedBitVector.buildPaletteMap(palette));
	}

	public CompactPalettedBitVector(List<T> palette, int size)
	{
		this(palette);
		int bits = size * this.bitsPerEntry;
		int arrayLength = (int)Math.ceil((double)bits / 64D);
		this.setArray(new long[arrayLength]);
	}

	public CompactPalettedBitVector(List<T> palette, long[] array)
	{
		this(palette);
		this.setArray(Arrays.copyOf(array, array.length));
	}

	protected void setArray(long[] array)
	{
		this.entryCount = array.length * this.entriesPerSlot;
		this.data = array;
	}

	public int getLength()
	{
		return this.entryCount;
	}

	public void set(int index, @Nonnull T value)
	{
		// I don't really feel confident reproducing this, so I copied it from https://github.com/maruohon/litematica/blob/08423854c5b647e4268633bc5b511d1c50a27f38/src/main/java/litematica/schematic/container/LitematicaBitArray.java
		Integer paletteIndex = this.palette.get(value);
		if(paletteIndex == null)
			throw new NoSuchElementException("Value not in palette");

		long startOffset = index * (long) this.bitsPerEntry;
		int startArrIndex = (int) (startOffset >> 6); // startOffset / 64
		int endArrIndex = (int) (((index + 1L) * (long) this.bitsPerEntry - 1L) >> 6);
		int startBitOffset = (int) (startOffset & 0x3F); // startOffset % 64
		this.data[startArrIndex] = this.data[startArrIndex] & ~(this.entryMask << startBitOffset) | ((long)paletteIndex & this.entryMask) << startBitOffset;

		if(startArrIndex != endArrIndex)
		{
			int endOffset = 64 - startBitOffset;
			int j1 = this.bitsPerEntry - endOffset;
			this.data[endArrIndex] = this.data[endArrIndex] >>> j1 << j1 | ((long)paletteIndex & this.entryMask) >> endOffset;
		}
	}

	@Nonnull
	public T get(int index)
	{
		// I don't really feel confident reproducing this, so I copied it from https://github.com/maruohon/litematica/blob/08423854c5b647e4268633bc5b511d1c50a27f38/src/main/java/litematica/schematic/container/LitematicaBitArray.java
		long startOffset = index * (long) this.bitsPerEntry;
		int startArrIndex = (int)(startOffset >> 6); // startOffset / 64
		int endArrIndex = (int) (((index + 1L) * (long) this.bitsPerEntry - 1L) >> 6);
		int startBitOffset = (int) (startOffset & 0x3F); // startOffset % 64

		int paletteIndex;
		if(startArrIndex == endArrIndex)
		{
			paletteIndex = (int)(this.data[startArrIndex] >>> startBitOffset & this.entryMask);
		}
		else
		{
			int endOffset = 64 - startBitOffset;
			paletteIndex = (int)((this.data[startArrIndex] >>> startBitOffset | this.data[endArrIndex] << endOffset) & this.entryMask);
		}

		T value = this.palette.inverse().get(paletteIndex);
		if(value == null)
			throw new NoSuchElementException("Index " + paletteIndex + " not in palette!");
		return value;
	}

	@Nonnull
	public CompactPalettedBitVector<T> copy()
	{
		CompactPalettedBitVector<T> copy = new CompactPalettedBitVector<T>(this.palette);
		copy.setArray(Arrays.copyOf(this.data, this.data.length));
		return copy;
	}

	@Nonnull
	@Override
	public Iterator<T> iterator()
	{
		return new CompactPalettedBitVectorIterator();
	}

	@Override
	public NBTTagLongArray serializeNBT()
	{
		return new NBTTagLongArray(this.data);
	}

	@Override
	public void deserializeNBT(NBTTagLongArray nbt)
	{
		try
		{
			long[] storedArr = (long[])NLA_DATA_FIELD.get(nbt);
			this.setArray(storedArr);
		}
		catch(IllegalAccessException exc)
		{
			throw new RuntimeException("Extraction of data array from NBT failed", exc);
		}
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.palette, this.entriesPerSlot, this.entryCount, this.entryMask, this.bitsPerEntry, Arrays.hashCode(this.data));
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof CompactPalettedBitVector))
			return false;
		CompactPalettedBitVector<?> other = (CompactPalettedBitVector<?>)obj;

		if(!Objects.equals(this.palette, other.palette))
			return false;

		return Arrays.equals(this.data, other.data);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		builder.append('[');
		for(int index = 0; index < this.entryCount; ++index)
		{
			builder.append(this.get(index));
			if(index > 64)
			{
				builder.append("...");
				break;
			}

			if(index != (this.entryCount - 1))
				builder.append(", ");
		}
		builder.append(']');
		return builder.toString();
	}

	private static <T> ImmutableBiMap<T, Integer> buildPaletteMap(List<T> palette)
	{
		if(palette.isEmpty())
			throw new IllegalArgumentException("Palette cannot be empty!");

		ImmutableBiMap.Builder<T, Integer> builder = ImmutableBiMap.builder();
		for(int index = 0; index < palette.size(); ++index)
			builder.put(palette.get(index), index);
		return builder.build();
	}

	public static <T> CompactPalettedBitVector<T> readFromNBT(List<T> palette, NBTTagLongArray nbt)
	{
		CompactPalettedBitVector<T> vector = new CompactPalettedBitVector<T>(palette);
		vector.deserializeNBT(nbt);
		return vector;
	}

	private class CompactPalettedBitVectorIterator implements Iterator<T>
	{
		private final int limit;
		private int index;

		public CompactPalettedBitVectorIterator()
		{
			this.limit = CompactPalettedBitVector.this.getLength() - 1;
			this.index = -1;
		}

		@Override
		public boolean hasNext()
		{
			return this.index < this.limit;
		}

		@Override
		public T next()
		{
			return CompactPalettedBitVector.this.get(++this.index);
		}
	}
}
