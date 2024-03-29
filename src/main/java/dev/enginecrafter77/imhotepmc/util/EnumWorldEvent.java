package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum EnumWorldEvent {
	SOUND_DISPENSER_DISPENSE(1000),
	SOUND_DISPENSER_FAIL(1001),
	SOUND_DISPENSER_LAUNCH(1002),
	SOUND_ENDEREYE_LAUNCH(1003),
	SOUND_FIREWORK_SHOOT(1004),
	SOUND_IRON_DOOR_OPEN(1005),
	SOUND_WOODEN_DOOR_OPEN(1006),
	SOUND_WOODEN_TRAPDOOR_OPEN(1007),
	SOUND_FENCE_GATE_OPEN(1008),
	SOUND_FIRE_EXTINGUISH(1009),
	/** Plays a record given the record item ID as data */
	PLAY_RECORD(1010),
	SOUND_IRON_DOOR_CLOSE(1011),
	SOUND_WOODEN_DOOR_CLOSE(1012),
	SOUND_WOODEN_TRAPDOOR_CLOSE(1013),
	SOUND_FENCE_GATE_CLOSE(1014),
	SOUND_GHAST_WARN(1015),
	SOUND_GHAST_SHOOT(1016),
	SOUND_ENDERDRAGON_SHOOT(1017),
	SOUND_BLAZE_SHOOT(1018),
	SOUND_ZOMBIE_ATTACK_WOODEN_DOOR(1019),
	SOUND_ZOMBIE_ATTACK_IRON_DOOR(1020),
	SOUND_ZOMBIE_BREAK_WOODEN_DOOR(1021),
	SOUND_WITHER_BREAK_BLOCK(1022),
	SOUND_WITHER_SHOOT(1024),
	SOUND_BAT_TAKEOFF(1025),
	SOUND_ZOMBIE_INFECT(1026),
	SOUND_ZOMBIE_VILLAGER_CONVERTED(1027),
	SOUND_ANVIL_DESTROY(1029),
	SOUND_ANVIL_USE(1030),
	SOUND_ANVIL_LAND(1031),
	SOUND_PORTAL_TRAVEL(1032),
	SOUND_CHORUS_FLOWER_GROW(1033),
	SOUND_CHORUS_FLOWER_DEATH(1034),
	SOUND_BREWING_STAND_BREW(1035),
	SOUND_IRON_TRAPDOOR_CLOSE(1036),
	SOUND_IRON_TRAPDOOR_OPEN(1037),
	SMOKE_PARTICLES(2000),
	BLOCK_BREAK(2001),
	POTION_BREAK_SPLASH(2002),
	POTION_BREAK_SPLASH_ALT(2007),
	ENDER_EYE_CRACK(2003),
	PARTICLES_FLAME_AND_SMOKE(2004),
	BONEMEAL_USE(2005),
	DRAGON_BREATH_IMPACT(2006),
	END_PORTAL_OPEN(3000),
	SOUND_ENDERDRAGON_GROWL(3001);

	private final int internalId;

	private EnumWorldEvent(int internalId)
	{
		this.internalId = internalId;
	}

	public void play(World world, BlockPos where, int data)
	{
		world.playEvent(this.internalId, where, data);
	}
}
