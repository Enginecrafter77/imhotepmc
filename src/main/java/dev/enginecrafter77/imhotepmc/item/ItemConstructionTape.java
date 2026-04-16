package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.marker.*;
import dev.enginecrafter77.imhotepmc.util.math.Edge3i;
import dev.enginecrafter77.imhotepmc.util.transaction.ItemStackTransaction;
import dev.enginecrafter77.imhotepmc.util.transaction.ItemStackTransactionTemplate;
import dev.enginecrafter77.imhotepmc.util.transaction.Transaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class ItemConstructionTape extends Item {
	public ItemConstructionTape()
	{
		super();
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "construction_tape"));
		this.setTranslationKey("construction_tape");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos clickedPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		AreaMarkHandler handler = worldIn.getCapability(CapabilityAreaMarker.AREA_HANDLER, null);
		if(handler == null)
			return EnumActionResult.PASS;

		AreaMarkingActor actor = player.getCapability(CapabilityAreaMarker.AREA_MARKING_ACTOR, null);
		if(actor == null)
			return EnumActionResult.PASS;

		MarkingAnchor linkingFrom = Optional.ofNullable(actor.getCurrentLinkingPosition()).map(handler::getAnchorAt).orElse(null);
		MarkingAnchor linkingTo = handler.getAnchorAt(clickedPos);

		if(linkingFrom == null)
		{
			if(linkingTo == null)
				return EnumActionResult.PASS; // player tried linking a non-marker
			actor.setCurrentLinkingPosition(linkingTo.getMarkerPosition());
		}
		else
		{
			// player clicked a non-marker block OR clicked the same marker
			if(linkingTo == null || Objects.equals(linkingFrom, linkingTo))
			{
				actor.setCurrentLinkingPosition(null);
				return EnumActionResult.SUCCESS;
			}

			AnchorConnectTransaction linkTransaction = new AnchorConnectTransaction(handler, actor, linkingFrom, linkingTo);
			if(!linkTransaction.canCommit())
			{
				player.sendStatusMessage(Objects.requireNonNull(getLinkingResultReply(linkTransaction.getOperationResult())), true);
				return EnumActionResult.FAIL;
			}

			int tapeRequired = this.getTapeCostForLinking(handler, linkingFrom, linkingTo);
			Transaction tapeTransaction = createTapeConsumeTransaction(player, tapeRequired);
			if(!tapeTransaction.canCommit())
			{
				player.sendStatusMessage(new TextComponentTranslation("message.not_enough_tape.text"), true);
				return EnumActionResult.FAIL;
			}

			linkTransaction.commit();
			tapeTransaction.commit();
			actor.setCurrentLinkingPosition(null);
		}
		return EnumActionResult.SUCCESS;
	}

	public int getTapeCostForLinking(AreaMarkHandler handler, MarkingAnchor a1, MarkingAnchor a2)
	{
		@Nullable MarkedArea m1 = handler.getAreaAnchoredAt(a1.getMarkerPosition());
		@Nullable MarkedArea m2 = handler.getAreaAnchoredAt(a2.getMarkerPosition());
		if(m1 != null && m2 != null)
			return -1;
		Edge3i out = new Edge3i();
		if(m1 == null && m2 == null)
		{
			if(tryConnect(a1, a2, out))
				return out.length();
			else
				return -1;
		}

		@Nonnull MarkedArea existing = m1 != null ? m1 : m2;
		if(findConnectingEdge(existing, a2, out))
			return out.length();
		else
			return -1;
	}

	@Nullable
	private static ITextComponent getLinkingResultReply(@Nullable AreaExpandResult result)
	{
		if(result == null)
			return null;
		switch(result)
		{
		case NO_CONNECTING_AXIS:
			return new TextComponentTranslation("message.anchors_not_aligned.text");
		case NO_EXPANDING_AXIS:
			return new TextComponentTranslation("message.anchors_not_contributing.text");
		case ALREADY_ADDED:
			return new TextComponentTranslation("message.anchors_already_connected.text");
		case CONFLICT:
			return new TextComponentTranslation("message.conflicting_mark_zones.text");
		default:
			return null;
		}
	}

	private static Transaction createTapeConsumeTransaction(EntityPlayer player, int tapeRequired)
	{
		if(player.isCreative())
			return Transaction.PLACEHOLDER;

		IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if(playerInventory == null)
			return Transaction.PLACEHOLDER;

		ItemStackTransactionTemplate template = ItemStackTransactionTemplate.builder()
				.consume(new ItemStack(ImhotepMod.ITEM_CONSTRUCTION_TAPE, tapeRequired))
				.build();
		ItemStackTransaction itemStackTransaction = new ItemStackTransaction(template);
		itemStackTransaction.setSource(playerInventory);
		itemStackTransaction.setDestination(playerInventory);
		return itemStackTransaction;
	}

	private static boolean findConnectingEdge(MarkedArea area, MarkingAnchor anchor, Edge3i out)
	{
		out.p1.set(anchor.getMarkerPosition().getX(), anchor.getMarkerPosition().getY(), anchor.getMarkerPosition().getZ());
		for(BlockPos member : area.getDefiningMembers())
		{
			out.p2.set(member.getX(), member.getY(), member.getZ());
			if(Edge3i.getConnectingEdgeAxis(out.p1, out.p2) != null)
				return true;
		}
		return false;
	}

	private static boolean tryConnect(MarkingAnchor a1, MarkingAnchor a2, Edge3i out)
	{
		out.p1.set(a1.getMarkerPosition().getX(), a1.getMarkerPosition().getY(), a1.getMarkerPosition().getZ());
		out.p2.set(a2.getMarkerPosition().getX(), a2.getMarkerPosition().getY(), a2.getMarkerPosition().getZ());
		return Edge3i.getConnectingEdgeAxis(out.p1, out.p2) != null;
	}
}
