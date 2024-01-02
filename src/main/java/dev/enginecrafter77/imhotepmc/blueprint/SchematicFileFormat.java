package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintCrossVersionTable;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;

public enum SchematicFileFormat {
	LITEMATICA(".litematic"),
	SCHEMATIC(".schematic"),
	SPONGE(".schem");

	private final String extension;

	private SchematicFileFormat(String extension)
	{
		this.extension = extension;
	}

	public NBTBlueprintSerializer createSerializer(@Nullable BlueprintCrossVersionTable table)
	{
		switch(this)
		{
		case LITEMATICA:
			return new LitematicaBlueprintSerializer(table);
		case SCHEMATIC:
			return new SchematicaBlueprintSerializer();
		case SPONGE:
			return new SpongeBlueprintSerializer(table);
		default:
			throw new UnsupportedOperationException();
		}
	}

	public static boolean isSchematic(File file)
	{
		return SchematicFileFormat.fromFile(file) != null;
	}

	public static boolean isSchematic(Path path)
	{
		return SchematicFileFormat.fromPath(path) != null;
	}

	@Nullable
	public static SchematicFileFormat fromPath(Path path)
	{
		Path fileName = path.getFileName();

		for(SchematicFileFormat format : SchematicFileFormat.values())
		{
			if(fileName.toString().endsWith(format.extension))
				return format;
		}
		return null;
	}

	@Nullable
	public static SchematicFileFormat fromFile(File file)
	{
		return fromPath(file.toPath());
	}

	public static void writeToBuffer(ByteBuf buffer, SchematicFileFormat format)
	{
		buffer.writeByte(format.ordinal());
	}

	public static SchematicFileFormat readFromBuffer(ByteBuf buffer)
	{
		return SchematicFileFormat.values()[buffer.readByte()];
	}
}
