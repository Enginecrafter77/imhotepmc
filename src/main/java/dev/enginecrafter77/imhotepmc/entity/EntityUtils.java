package dev.enginecrafter77.imhotepmc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;

public class EntityUtils {
	private static final double GRAVITY_PULL = 0.03999999910593033D;
	private static final double AIR_DRAG_FACTOR = 0.9800000190734863D;
	private static final double FRICTION_FACTOR = 0.699999988079071D;

	/**
	 * Applies basic physics to an entity. Taken from vanilla minecraft and optimized for readability.
	 * @param entity The entity to apply the physics to
	 */
	public static void applyBasicPhysics(Entity entity)
	{
		// Rotate position
		entity.prevPosX = entity.posX;
		entity.prevPosY = entity.posY;
		entity.prevPosZ = entity.posZ;

		// Apply gravity (!entity.hasNoGravity() == entity.hasGravity() [hypothetical])
		if(!entity.hasNoGravity())
			entity.motionY -= GRAVITY_PULL;

		// Move the entity
		entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);

		// Apply air drag?
		entity.motionX *= AIR_DRAG_FACTOR;
		entity.motionY *= AIR_DRAG_FACTOR;
		entity.motionZ *= AIR_DRAG_FACTOR;

		if(entity.onGround)
		{
			entity.motionX *= FRICTION_FACTOR;
			entity.motionZ *= FRICTION_FACTOR;
			entity.motionY *= -0.5D; // magic constant, why?? (taken from vanilla)
		}
	}
}
