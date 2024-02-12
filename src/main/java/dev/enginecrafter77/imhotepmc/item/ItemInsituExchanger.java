package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import dev.enginecrafter77.imhotepmc.util.ServerBackgroundTaskScheduler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ItemInsituExchanger extends Item {
	private static final String NBT_KEY_ENERGY = "Energy";
	private static final String NBT_KEY_ITEMTAG = "Tag";
	private static final String NBT_KEY_ACTIVE = "Active";

	public ItemInsituExchanger()
	{
		super();
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "insitu_exchanger"));
		this.setTranslationKey("insitu_exchanger");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Override
	public int getMetadata(ItemStack stack)
	{
		return this.isExchangeActive(stack) ? 1 : 0;
	}

	private void setExchangeActive(ItemStack stack, boolean active)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			tag = new NBTTagCompound();
		tag.setBoolean(NBT_KEY_ACTIVE, active);
		stack.setTagCompound(tag);
	}

	public boolean isExchangeActive(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return false;
		return tag.getBoolean(NBT_KEY_ACTIVE);
	}

	protected void onExchangeStart(ItemStack stack)
	{
		this.setExchangeActive(stack, true);
	}

	protected void onExchangeStop(ItemStack stack)
	{
		this.setExchangeActive(stack, false);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote || hand != EnumHand.MAIN_HAND)
			return EnumActionResult.SUCCESS;

		ItemStack exchangerStack = player.getHeldItem(EnumHand.MAIN_HAND);
		ItemStack offHandStack = player.getHeldItem(EnumHand.OFF_HAND);

		if(this.isExchangeActive(exchangerStack))
			return EnumActionResult.SUCCESS;

		IBlockState replacement = this.getReplacementBlockFromStack(offHandStack);
		if(replacement == null)
			return EnumActionResult.SUCCESS;

		IEnergyStorage energyStorage = exchangerStack.getCapability(CapabilityEnergy.ENERGY, null);
		if(energyStorage == null)
			throw new IllegalStateException();

		@Nullable IItemHandler inv = player.isCreative() ? null : player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		ConnectedReplaceTask task = new ConnectedReplaceTask(worldIn, player, energyStorage, inv);
		task.setup(pos, replacement);
		task.setOnStartCallback(() -> this.onExchangeStart(exchangerStack));
		task.setOnCompleteCallback(() -> this.onExchangeStop(exchangerStack));
		ImhotepMod.instance.getBackgroundTaskScheduler().enqueue(task);

		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
	{
		return false;
	}

	@Nullable
	private IBlockState getReplacementBlockFromStack(ItemStack stack)
	{
		Item offHandItem = stack.getItem();
		if(offHandItem instanceof ItemBlock)
		{
			ItemBlock itemBlock = (ItemBlock)offHandItem;
			int meta = itemBlock.getMetadata(stack);
			Block block = itemBlock.getBlock();
			return block.getStateFromMeta(meta);
		}
		else if(offHandItem instanceof ItemBlockSpecial)
		{
			ItemBlockSpecial itemBlock = (ItemBlockSpecial)offHandItem;
			int meta = itemBlock.getMetadata(stack);
			Block block = itemBlock.getBlock();
			return block.getStateFromMeta(meta);
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);

		IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		if(energyStorage != null)
			tooltip.add(new TextComponentTranslation("label.energy.fill", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()).getFormattedText());

		tooltip.add("");
		tooltip.add(new TextComponentTranslation("desc.insitu_exchanger.0").getFormattedText());
		tooltip.add(new TextComponentTranslation("desc.insitu_exchanger.1").getFormattedText());
		tooltip.add("");
		tooltip.add(new TextComponentTranslation("desc.insitu_exchanger.2").getFormattedText());
		tooltip.add(new TextComponentTranslation("desc.insitu_exchanger.3").getFormattedText());
		tooltip.add(new TextComponentTranslation("desc.insitu_exchanger.4").getFormattedText());
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
	{
		return new InsituExchangerCapabilityContainer();
	}

	@Override
	public boolean getShareTag()
	{
		return true;
	}

	@Nullable
	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack)
	{
		NBTTagCompound tag = new NBTTagCompound();

		NBTTagCompound itemtag = super.getNBTShareTag(stack);
		if(itemtag != null)
			tag.setTag(NBT_KEY_ITEMTAG, itemtag);
		tag.setTag(NBT_KEY_ENERGY, Objects.requireNonNull(CapabilityEnergy.ENERGY.writeNBT(Objects.requireNonNull(stack.getCapability(CapabilityEnergy.ENERGY, null)), null)));

		return tag;
	}

	@Override
	public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
	{
		if(nbt == null)
		{
			super.readNBTShareTag(stack, null);
			return;
		}

		super.readNBTShareTag(stack, nbt.getCompoundTag(NBT_KEY_ITEMTAG));
		NBTBase energyTag = nbt.getTag(NBT_KEY_ENERGY);
		CapabilityEnergy.ENERGY.readNBT(Objects.requireNonNull(stack.getCapability(CapabilityEnergy.ENERGY, null)), null, energyTag);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);
		if(energy == null)
			return 0D;
		return 1D - ((double)energy.getEnergyStored() / (double)energy.getMaxEnergyStored());
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return this.getDurabilityForDisplay(stack) < 1D;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged;
	}

	@SubscribeEvent
	public static void interceptLeftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World worldIn = player.world;
		if(worldIn.isRemote)
			return;

		ItemInsituExchanger instance = ImhotepMod.ITEM_INSITU_EXCHANGER;
		ItemStack exchangerStack = player.getHeldItem(EnumHand.MAIN_HAND);
		if(!(exchangerStack.getItem() instanceof ItemInsituExchanger) || instance.isExchangeActive(exchangerStack))
			return;

		ItemStack offHandStack = player.getHeldItem(EnumHand.OFF_HAND);
		if(instance.isExchangeActive(exchangerStack))
			return;

		IBlockState existingOccupant = worldIn.getBlockState(event.getPos());
		IBlockState replacement = instance.getReplacementBlockFromStack(offHandStack);
		if(replacement == null || Objects.equals(existingOccupant, replacement))
			return;

		IEnergyStorage energyStorage = exchangerStack.getCapability(CapabilityEnergy.ENERGY, null);
		if(energyStorage == null)
			throw new IllegalStateException();

		@Nullable IItemHandler inv = player.isCreative() ? null : player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		ConnectedReplaceTask task = new ConnectedReplaceTask(worldIn, player, energyStorage, inv);
		task.setup(event.getPos(), replacement);
		task.setBlockLimit(1);
		task.runBlocking();

		event.setCanceled(true);
	}

	public static class InsituExchangerCapabilityContainer implements ICapabilitySerializable<NBTTagCompound>
	{
		private final IEnergyStorage energyStorage;

		public InsituExchangerCapabilityContainer()
		{
			this.energyStorage = new EnergyStorage(64000, 1000, 1000);
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
		{
			return capability == CapabilityEnergy.ENERGY;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
		{
			if(capability == CapabilityEnergy.ENERGY)
				return CapabilityEnergy.ENERGY.cast(this.energyStorage);
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound tag = new NBTTagCompound();
			NBTBase energyTag = CapabilityEnergy.ENERGY.writeNBT(this.energyStorage, null);
			if(energyTag != null)
				tag.setTag(NBT_KEY_ENERGY, energyTag);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			CapabilityEnergy.ENERGY.readNBT(this.energyStorage, null, nbt.getTag(NBT_KEY_ENERGY));
		}
	}

	public static class ConnectedReplaceTask extends ServerBackgroundTaskScheduler.ServerBackgroundTask
	{
		private final Queue<BlockPos> queued;
		private final World world;
		private final Entity holder;

		@Nullable
		private final IItemHandler inventory;

		@Nullable
		private final IEnergyStorage energyStorage;

		private int blockLimit;
		private int replacedBlocks;
		private float distanceLimit;
		private IBlockState replace;
		private IBlockState replacement;
		private BlockPos origin;

		public ConnectedReplaceTask(World world, Entity holder, @Nullable IEnergyStorage energyStorage, @Nullable IItemHandler inventory)
		{
			this.holder = holder;
			this.energyStorage = energyStorage;
			this.queued = new LinkedBlockingQueue<BlockPos>();
			this.world = world;
			this.inventory = inventory;
			this.distanceLimit = 64F;
			this.blockLimit = -1;
			this.replacedBlocks = 0;
		}

		public void setup(BlockPos origin, IBlockState replaceWith)
		{
			this.queued.clear();
			this.queued.add(origin);
			this.origin = origin;
			this.replace = this.world.getBlockState(origin);
			this.replacement = replaceWith;
			this.replacedBlocks = 0;
		}

		public void setDistanceLimit(float distanceLimit)
		{
			this.distanceLimit = distanceLimit;
		}

		public void setBlockLimit(int blockLimit)
		{
			this.blockLimit = blockLimit;
		}

		public void removeBlockLimit()
		{
			this.blockLimit = -1;
		}

		public int getReplaceEnergyConsumed(BlockPos pos)
		{
			float hardness = this.replace.getBlockHardness(this.world, pos);
			if(hardness == -1.0F) // unbreakable
				return Integer.MAX_VALUE;
			float baseReplaceCost = hardness * 50F + 10F;

			float distance = (float)Math.sqrt(this.holder.getDistanceSqToCenter(pos));
			float distanceMultiplier = Math.min(5F, distance * 0.25F);
			return Math.round(distanceMultiplier * baseReplaceCost);
		}

		@Override
		public void update()
		{
			if(this.blockLimit != -1 && this.replacedBlocks >= this.blockLimit)
			{
				this.markComplete();
				return;
			}

			BlockPos pos;
			do
			{
				if(this.queued.isEmpty())
				{
					this.markComplete();
					return;
				}
				pos = this.queued.remove();
			}
			while(!this.isBlockCandidateForReplacement(pos));

			ItemStack consume = new ItemStack(this.replacement.getBlock(), 1, this.replacement.getBlock().getMetaFromState(this.replacement));
			ItemStack reclaim = new ItemStack(this.replace.getBlock(), 1, this.replace.getBlock().getMetaFromState(this.replace));
			int consumeSlot = -1, reclaimSlot = -1;
			if(this.inventory != null)
			{
				for(int slot = 0; slot < this.inventory.getSlots(); ++slot)
				{
					if(reclaimSlot == -1 && this.inventory.insertItem(slot, reclaim, true).isEmpty())
						reclaimSlot = slot;

					ItemStack wouldBeConsumed = this.inventory.extractItem(slot, consume.getCount(), true);
					if(consumeSlot == -1 && ItemStack.areItemStacksEqual(wouldBeConsumed, consume))
						consumeSlot = slot;
				}

				if(consumeSlot == -1 || reclaimSlot == -1)
					return;
			}

			int requiredEnergy = this.getReplaceEnergyConsumed(pos);
			if(this.energyStorage != null)
			{
				int disposableEnergy = this.energyStorage.extractEnergy(requiredEnergy, true);
				if(disposableEnergy < requiredEnergy)
					return;
			}

			if(this.inventory != null)
			{
				this.inventory.extractItem(consumeSlot, 1, false);
				this.inventory.insertItem(reclaimSlot, reclaim, false);
			}

			if(this.energyStorage != null)
				this.energyStorage.extractEnergy(requiredEnergy, false);

			this.world.playEvent(2001, pos, Block.getStateId(this.replace));
			this.world.setBlockState(pos, this.replacement);
			++this.replacedBlocks;

			BlockPosUtil.neighbors(pos).forEach(this.queued::add);
		}

		private boolean isBlockCandidateForReplacement(BlockPos pos)
		{
			double distance = Math.sqrt(pos.distanceSq(this.origin));
			if(distance > this.distanceLimit)
				return false;
			IBlockState state = this.world.getBlockState(pos);
			return Objects.equals(state, this.replace);
		}
	}
}
