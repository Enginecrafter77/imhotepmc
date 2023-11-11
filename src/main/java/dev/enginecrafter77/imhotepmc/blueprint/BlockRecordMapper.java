package dev.enginecrafter77.imhotepmc.blueprint;

import javax.annotation.Nullable;

public interface BlockRecordMapper {
	@Nullable
	public SavedTileState translate(SavedTileState state);

	public static BlockRecordMapper identity()
	{
		return (SavedTileState state) -> state;
	}
}
