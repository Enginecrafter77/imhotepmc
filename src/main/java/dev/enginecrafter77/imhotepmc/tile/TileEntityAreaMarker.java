package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Objects;

public class TileEntityAreaMarker extends TileEntity {
	private static final String NBT_KEY_IS_LINKED = "is_linked";
	private static final String NBT_KEY_LINK = "link";

	@Nullable
	private MarkerLink link;

	public TileEntityAreaMarker()
	{
		this.link = null;
	}

	public boolean isLinked()
	{
		return this.link != null;
	}

	public void selectLinkedArea(BlockSelectionBox box)
	{
		if(this.link == null)
			return;
		this.link.select(box);
	}

	public void link(BlockPos other)
	{
		TileEntity otherTile = this.world.getTileEntity(other);
		if(!(otherTile instanceof TileEntityAreaMarker))
			return;
		TileEntityAreaMarker otherMarker = (TileEntityAreaMarker)otherTile;

		this.link = MarkerLink.begin(this.getPos(), other);
		otherMarker.link = this.link.peer();
	}

	public void unlink()
	{
		if(this.link != null)
		{
			TileEntityAreaMarker otherMarker = (TileEntityAreaMarker)this.world.getTileEntity(this.link.getPeerPosition());
			if(otherMarker != null)
				otherMarker.link = null;
		}
		this.link = null;
	}

	@Nullable
	public MarkerLink getLink()
	{
		return this.link;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);

		boolean linked = compound.getBoolean(NBT_KEY_IS_LINKED);
		if(linked)
		{
			NBTTagCompound linkTag = compound.getCompoundTag(NBT_KEY_LINK);
			this.link = MarkerLink.fromNBT(linkTag);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setBoolean(NBT_KEY_IS_LINKED, this.link != null);
		if(this.link != null)
			compound.setTag(NBT_KEY_LINK, this.link.toNBT());
		return compound;
	}

	public static class MarkerLink
	{
		private final BlockPos me;
		private final BlockPos peer;
		private final boolean master;

		protected MarkerLink(BlockPos me, BlockPos peer, boolean master)
		{
			this.me = me;
			this.peer = peer;
			this.master = master;
		}

		public void select(BlockSelectionBox box)
		{
			int cmp = this.me.compareTo(this.peer);
			if(cmp < 0)
			{
				box.setStart(this.me);
				box.setEnd(this.peer);
			}
			else if(cmp > 0)
			{
				box.setStart(this.peer);
				box.setEnd(this.me);
			}
			else
			{
				throw new IllegalStateException();
			}
		}

		public BlockPos getMyPosition()
		{
			return this.me;
		}

		public BlockPos getPeerPosition()
		{
			return this.peer;
		}

		public boolean isMaster()
		{
			return this.master;
		}

		public MarkerLink peer()
		{
			return new MarkerLink(this.peer, this.me, false);
		}

		public NBTTagCompound toNBT()
		{
			NBTTagCompound tag = new NBTTagCompound();

			tag.setTag("me", NBTUtil.createPosTag(this.me));
			tag.setTag("peer", NBTUtil.createPosTag(this.peer));
			tag.setBoolean("master", this.master);

			return tag;
		}

		public static MarkerLink begin(BlockPos me, BlockPos peer)
		{
			if(Objects.equals(me, peer))
				throw new IllegalArgumentException();
			return new MarkerLink(me, peer, true);
		}

		public static MarkerLink fromNBT(NBTTagCompound tag)
		{
			NBTTagCompound mTag = tag.getCompoundTag("me");
			NBTTagCompound pTag = tag.getCompoundTag("peer");
			boolean master = tag.getBoolean("master");

			BlockPos me = NBTUtil.getPosFromTag(mTag);
			BlockPos peer = NBTUtil.getPosFromTag(pTag);

			return new MarkerLink(me, peer, master);
		}
	}
}
