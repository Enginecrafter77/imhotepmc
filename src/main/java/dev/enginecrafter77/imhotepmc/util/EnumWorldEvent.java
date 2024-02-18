package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum EnumWorldEvent {
	BLOCK_BREAK(2001);

	private final int internalId;

	private EnumWorldEvent(int internalId)
	{
		this.internalId = internalId;
	}

	public void play(World world, BlockPos where, int data)
	{
		world.playEvent(this.internalId, where, data);
	}

	/*
	switch (type)
		{
		case 1000:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			break;
		case 1001:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
			break;
		case 1002:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
			break;
		case 1003:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
			break;
		case 1004:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_FIREWORK_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
			break;
		case 1005:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1006:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1007:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1008:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1009:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
			break;
		case 1010:

			if (Item.getItemById(data) instanceof ItemRecord)
			{
				this.world.playRecord(blockPosIn, ((ItemRecord)Item.getItemById(data)).getSound());
			}
			else
			{
				this.world.playRecord(blockPosIn, (SoundEvent)null);
			}

			break;
		case 1011:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1012:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1013:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1014:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1015:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1016:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1017:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1018:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1019:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1020:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1021:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1022:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1024:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1025:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1026:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1027:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;
		case 1029:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1030:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1031:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1032:
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F));
			break;
		case 1033:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			break;
		case 1034:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			break;
		case 1035:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			break;
		case 1036:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 1037:
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 2000:
			int j2 = data % 3 - 1;
			int k1 = data / 3 % 3 - 1;
			double d11 = (double)blockPosIn.getX() + (double)j2 * 0.6D + 0.5D;
			double d13 = (double)blockPosIn.getY() + 0.5D;
			double d15 = (double)blockPosIn.getZ() + (double)k1 * 0.6D + 0.5D;

			for (int l2 = 0; l2 < 10; ++l2)
			{
				double d16 = random.nextDouble() * 0.2D + 0.01D;
				double d19 = d11 + (double)j2 * 0.01D + (random.nextDouble() - 0.5D) * (double)k1 * 0.5D;
				double d22 = d13 + (random.nextDouble() - 0.5D) * 0.5D;
				double d25 = d15 + (double)k1 * 0.01D + (random.nextDouble() - 0.5D) * (double)j2 * 0.5D;
				double d27 = (double)j2 * d16 + random.nextGaussian() * 0.01D;
				double d29 = -0.03D + random.nextGaussian() * 0.01D;
				double d30 = (double)k1 * d16 + random.nextGaussian() * 0.01D;
				this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d19, d22, d25, d27, d29, d30);
			}

			return;
		case 2001:
			Block block = Block.getBlockById(data & 4095);

			if (block.getDefaultState().getMaterial() != Material.AIR)
			{
				SoundType soundtype = block.getSoundType(Block.getStateById(data), world, blockPosIn, null);
				this.world.playSound(blockPosIn, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
			}

			this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 255));
			break;
		case 2002:
		case 2007:
			double d9 = (double)blockPosIn.getX();
			double d10 = (double)blockPosIn.getY();
			double d12 = (double)blockPosIn.getZ();

			for (int k2 = 0; k2 < 8; ++k2)
			{
				this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d9, d10, d12, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, Item.getIdFromItem(Items.SPLASH_POTION));
			}

			float f5 = (float)(data >> 16 & 255) / 255.0F;
			float f = (float)(data >> 8 & 255) / 255.0F;
			float f1 = (float)(data >> 0 & 255) / 255.0F;
			EnumParticleTypes enumparticletypes = type == 2007 ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;

			for (int j3 = 0; j3 < 100; ++j3)
			{
				double d18 = random.nextDouble() * 4.0D;
				double d21 = random.nextDouble() * Math.PI * 2.0D;
				double d24 = Math.cos(d21) * d18;
				double d26 = 0.01D + random.nextDouble() * 0.5D;
				double d28 = Math.sin(d21) * d18;
				Particle particle1 = this.spawnParticle0(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d9 + d24 * 0.1D, d10 + 0.3D, d12 + d28 * 0.1D, d24, d26, d28);

				if (particle1 != null)
				{
					float f4 = 0.75F + random.nextFloat() * 0.25F;
					particle1.setRBGColorF(f5 * f4, f * f4, f1 * f4);
					particle1.multiplyVelocity((float)d18);
				}
			}

			this.world.playSound(blockPosIn, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 2003:
			double d3 = (double)blockPosIn.getX() + 0.5D;
			double d4 = (double)blockPosIn.getY();
			double d5 = (double)blockPosIn.getZ() + 0.5D;

			for (int l1 = 0; l1 < 8; ++l1)
			{
				this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d3, d4, d5, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, Item.getIdFromItem(Items.ENDER_EYE));
			}

			for (double d14 = 0.0D; d14 < (Math.PI * 2D); d14 += 0.15707963267948966D)
			{
				this.spawnParticle(EnumParticleTypes.PORTAL, d3 + Math.cos(d14) * 5.0D, d4 - 0.4D, d5 + Math.sin(d14) * 5.0D, Math.cos(d14) * -5.0D, 0.0D, Math.sin(d14) * -5.0D);
				this.spawnParticle(EnumParticleTypes.PORTAL, d3 + Math.cos(d14) * 5.0D, d4 - 0.4D, d5 + Math.sin(d14) * 5.0D, Math.cos(d14) * -7.0D, 0.0D, Math.sin(d14) * -7.0D);
			}

			return;
		case 2004:

			for (int i3 = 0; i3 < 20; ++i3)
			{
				double d17 = (double)blockPosIn.getX() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
				double d20 = (double)blockPosIn.getY() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
				double d23 = (double)blockPosIn.getZ() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
				this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d17, d20, d23, 0.0D, 0.0D, 0.0D, new int[0]);
				this.world.spawnParticle(EnumParticleTypes.FLAME, d17, d20, d23, 0.0D, 0.0D, 0.0D, new int[0]);
			}

			return;
		case 2005:
			ItemDye.spawnBonemealParticles(this.world, blockPosIn, data);
			break;
		case 2006:

			for (int i2 = 0; i2 < 200; ++i2)
			{
				float f2 = random.nextFloat() * 4.0F;
				float f3 = random.nextFloat() * ((float)Math.PI * 2F);
				double d6 = (double)(MathHelper.cos(f3) * f2);
				double d7 = 0.01D + random.nextDouble() * 0.5D;
				double d8 = (double)(MathHelper.sin(f3) * f2);
				Particle particle = this.spawnParticle0(EnumParticleTypes.DRAGON_BREATH.getParticleID(), false, (double)blockPosIn.getX() + d6 * 0.1D, (double)blockPosIn.getY() + 0.3D, (double)blockPosIn.getZ() + d8 * 0.1D, d6, d7, d8);

				if (particle != null)
				{
					particle.multiplyVelocity(f2);
				}
			}

			this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_FIREBALL_EPLD, SoundCategory.HOSTILE, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
			break;
		case 3000:
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, true, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
			this.world.playSound(blockPosIn, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
			break;
		case 3001:
			this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.world.rand.nextFloat() * 0.3F, false);
		}
	 */
}
