package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.BlockPos;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public interface BlockAnchor {
	public static final BlockAnchor END = offset(new Vector3d(1D, 1D, 1D));
	public static final BlockAnchor START = offset(new Vector3d(0D, 0D, 0D));
	public static final BlockAnchor CENTER = offset(new Vector3d(0.5D, 0.5D, 0.5D));

	public void anchorToBlock(BlockPos pos, Tuple3d anchor);

	public static BlockAnchor offset(Tuple3d offset)
	{
		return (BlockPos pos, Tuple3d anchor) -> {
			anchor.set(pos.getX(), pos.getY(), pos.getZ());
			anchor.add(offset);
		};
	}
}
