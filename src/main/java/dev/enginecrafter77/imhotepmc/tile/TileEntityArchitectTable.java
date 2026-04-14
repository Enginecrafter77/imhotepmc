package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.*;
import dev.enginecrafter77.imhotepmc.marker.AreaMarkHandler;
import dev.enginecrafter77.imhotepmc.marker.CapabilityAreaMarker;
import dev.enginecrafter77.imhotepmc.marker.MarkedArea;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import dev.enginecrafter77.imhotepmc.util.VecNBTUtil;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntityArchitectTable extends TileEntity {
	private static final String NBT_KEY_SELECTION = "selection";
	private static final String NBT_KEY_INITIALIZED = "initialized";
	private static final String NBT_KEY_INVENTORY = "inventory";

	private final ItemStackHandler inventory;

	private final Box3i selection;

	@Nullable
	private AxisAlignedBB renderBox;

	private boolean initialized;

	public TileEntityArchitectTable()
	{
		this.renderBox = null;
		this.inventory = new ItemStackHandler(1);
		this.selection = new Box3i();
		this.initialized = false;
	}

	public Box3i getSelection()
	{
		return this.selection;
	}

	public StructureBlueprint scanStructure()
	{
		BlockPos origin = new BlockPos(this.selection.start.x, this.selection.start.y, this.selection.start.z);

		BlueprintEditor blueprintEditor = StructureBlueprint.begin();
		blueprintEditor.setSize(new Vec3i(this.selection.getSizeX(), this.selection.getSizeY(), this.selection.getSizeZ()));
		for(BlockPos pos : BlockPos.MutableBlockPos.getAllInBoxMutable(this.selection.start.x, this.selection.start.y, this.selection.start.z, this.selection.end.x, this.selection.end.y, this.selection.end.z))
		{
			SavedTileState sts = SavedTileState.sample(this.world, pos);
			blueprintEditor.addBlock(pos.subtract(origin), sts);
		}
		return blueprintEditor.build();
	}

	public SchematicBlueprint createSchematic(SchematicMetadata metadata)
	{
		StructureBlueprint blueprint = this.scanStructure();

		SchematicEditor schematicBuilder = SchematicBlueprint.builder();
		schematicBuilder.addRegion(metadata.getName(), blueprint, BlockPos.ORIGIN);
		schematicBuilder.setMetadata(metadata);
		return schematicBuilder.build();
	}

	public boolean scanToBlueprintItem(SchematicMetadata metadata)
	{
		ItemStack stack = this.inventory.getStackInSlot(0);
		if(stack.getItem() != ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT)
			return false;
		SchematicBlueprint blueprint = this.createSchematic(metadata);
		ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT.setSchematic(stack, blueprint);
		return true;
	}

	public boolean isInitialized()
	{
		return this.initialized;
	}

	protected void onSelectionUpdated()
	{
		this.renderBox = null;
	}

	@Override
	public void onLoad()
	{
		super.onLoad();
		BlockPosUtil.neighbors(this.getPos()).forEach((BlockPos neighbor) -> {
			if(this.initialized)
				return;

			AreaMarkHandler handler = this.world.getCapability(CapabilityAreaMarker.AREA_HANDLER, null);
			if(handler == null)
				return;

			MarkedArea area = handler.getAreaAnchoredAt(neighbor);
			if(area == null)
				return;

			this.selection.set(area.getMarkedAreaBox());
			handler.dismantle(area.getId());
			this.initialized = true;
			this.onSelectionUpdated();
			this.markDirty();
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		if(this.renderBox == null)
			this.renderBox = VecUtil.boxToAABB(this.selection);
		return this.renderBox;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.inventory.deserializeNBT(compound.getCompoundTag(NBT_KEY_INVENTORY));
		VecNBTUtil.deserializeBox3iFromNBT(compound.getTag(NBT_KEY_SELECTION), this.selection);
		this.initialized = compound.getBoolean(NBT_KEY_INITIALIZED);
		this.onSelectionUpdated();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setTag(NBT_KEY_INVENTORY, this.inventory.serializeNBT());
		compound.setTag(NBT_KEY_SELECTION, VecNBTUtil.serializeBox3iToNBT(this.selection));
		compound.setBoolean(NBT_KEY_INITIALIZED, this.initialized);
		return compound;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory);
		return super.getCapability(capability, facing);
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

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.getPos(), 0, this.serializeNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		this.deserializeNBT(pkt.getNbtCompound());
	}
}
