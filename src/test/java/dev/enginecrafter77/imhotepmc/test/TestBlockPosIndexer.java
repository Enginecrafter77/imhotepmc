package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import dev.enginecrafter77.imhotepmc.blueprint.NaturalVoxelIndexer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBlockPosIndexer {
	@Test
	public void testSimple2x2()
	{
		Vec3i size = new Vec3i(2, 2, 2);
		VoxelIndexer indexer = NaturalVoxelIndexer.inVolume(size);

		assertBlockPos(indexer, 0, "0:0:0");
		assertBlockPos(indexer, 1, "1:0:0");
		assertBlockPos(indexer, 2, "0:0:1");
		assertBlockPos(indexer, 3, "1:0:1");
		assertBlockPos(indexer, 4, "0:1:0");
		assertBlockPos(indexer, 5, "1:1:0");
		assertBlockPos(indexer, 6, "0:1:1");
		assertBlockPos(indexer, 7, "1:1:1");
	}

	@Test
	public void testMatchingPairs()
	{
		Vec3i size = new Vec3i(16, 16, 16);
		VoxelIndexer indexer = NaturalVoxelIndexer.inVolume(size);

		for(int index = 0; index < indexer.getVolume(); ++index)
		{
			BlockPos pos = indexer.fromIndex(index);
			int rec = indexer.toIndex(pos);
			Assertions.assertEquals(index, rec);
		}
	}

	@Test
	public void testPositionNotOutsideBounds()
	{
		Vec3i size = new Vec3i(16, 16, 16);
		VoxelIndexer indexer = NaturalVoxelIndexer.inVolume(size);

		for(int index = 0; index < indexer.getVolume(); ++index)
		{
			BlockPos pos = indexer.fromIndex(index);

			Assertions.assertTrue(pos.getX() < size.getX());
			Assertions.assertTrue(pos.getY() < size.getY());
			Assertions.assertTrue(pos.getZ() < size.getZ());
		}
	}

	private void assertBlockPos(VoxelIndexer indexer, int index, String spec)
	{
		BlockPos pos = this.parse(spec);
		BlockPos rec = indexer.fromIndex(index);
		Assertions.assertEquals(pos, rec);
	}

	private BlockPos parse(String spec)
	{
		String[] parts = spec.split(":");
		int x = Integer.parseInt(parts[0]);
		int y = Integer.parseInt(parts[1]);
		int z = Integer.parseInt(parts[2]);
		return new BlockPos(x, y, z);
	}
}
