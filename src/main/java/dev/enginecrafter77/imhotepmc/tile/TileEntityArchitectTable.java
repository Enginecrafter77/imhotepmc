package dev.enginecrafter77.imhotepmc.tile;

import com.google.common.collect.ImmutableList;
import dev.enginecrafter77.imhotepmc.blueprint.*;
import dev.enginecrafter77.imhotepmc.util.BlockPosEdge;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collection;

public class TileEntityArchitectTable extends TileEntity implements ITickable {
	private static final String NBT_KEY_SELECTION = "selection";
	private static final String NBT_KEY_INITIALIZED = "initialized";

	private final BlockSelectionBox selection;

	@Nonnull
	private Collection<BlockPosEdge> edges;

	private boolean initialized;
	private int scanDelay;

	public TileEntityArchitectTable()
	{
		this.edges = ImmutableList.of();
		this.selection = new BlockSelectionBox();
		this.initialized = false;
		this.scanDelay = 10;
	}

	public void getArea(BlockSelectionBox dest)
	{
		dest.set(this.selection);
	}

	public SchematicBlueprint sample()
	{
		BlueprintEditor blueprintEditor = StructureBlueprint.begin();
		for(BlockPos pos : this.selection.internalBlocks())
		{
			SavedTileState sts = SavedTileState.sample(this.world, pos);
			blueprintEditor.addBlock(pos.toImmutable(), sts);
		}

		MutableSchematicMetadata msm = new MutableSchematicMetadata();
		msm.setDescription("Created by ImhotepMC");

		SchematicBlueprint.Builder schematicBuilder = SchematicBlueprint.builder();
		schematicBuilder.addRegion("Unnamed", blueprintEditor.build(), BlockPos.ORIGIN);
		schematicBuilder.setMetadata(msm);
		return schematicBuilder.build();
	}

	public boolean isInitialized()
	{
		return this.initialized;
	}

	protected void onSelectionUpdated(BlockSelectionBox box)
	{
		this.edges = BlockPosUtil.findEdges(ImmutableList.of(box.getStart(), box.getEnd()));
	}

	public Collection<BlockPosEdge> getSelectionEdges()
	{
		return this.edges;
	}

	@Override
	public void update()
	{
		if(this.scanDelay > 0)
		{
			--this.scanDelay;
			return;
		}

		if(this.initialized)
			return;

		BlockPosUtil.neighbors(this.getPos()).forEach((BlockPos neighbor) -> {
			if(this.initialized)
				return;

			TileEntity tile = this.world.getTileEntity(neighbor);
			if(!(tile instanceof IAreaMarker))
				return;
			IAreaMarker marker = (IAreaMarker)tile;

			AreaMarkGroup group = marker.getCurrentMarkGroup();
			if(group == null)
				return;

			group.select(this.selection);
			group.dismantle(this.world);
			for(BlockPos corner : group.getDefiningCorners())
				this.world.destroyBlock(corner, true);
			this.initialized = true;
			this.onSelectionUpdated(this.selection);
			this.markDirty();
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		if(!this.initialized)
			return super.getRenderBoundingBox();

		return BlockPosUtil.contain(ImmutableList.of(this.getPos(), this.selection.getEnd()));
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.selection.deserializeNBT(compound.getCompoundTag(NBT_KEY_SELECTION));
		this.initialized = compound.getBoolean(NBT_KEY_INITIALIZED);

		if(this.initialized)
			this.onSelectionUpdated(this.selection);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setTag(NBT_KEY_SELECTION, this.selection.serializeNBT());
		compound.setBoolean(NBT_KEY_INITIALIZED, this.initialized);
		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.serializeNBT();
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		this.deserializeNBT(tag);
	}
}
