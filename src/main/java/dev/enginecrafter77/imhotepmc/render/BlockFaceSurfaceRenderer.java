package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.util.math.Rect2d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

import javax.vecmath.*;

public class BlockFaceSurfaceRenderer implements IRenderable {
	private static final Vector3d SHIFT_TO_CENTER = new Vector3d(0.5D, 0.5D, 0.5D);

	private final TextureSliceRender render;

	private final Rect2d surfaceRectangle;

	private final Point3d spriteRenderOffset;
	private final Vector3d spriteSurfaceTranslation;
	private final Vector3d spriteFacingVector;

	private EnumFacing facing;
	private double lift;

	public BlockFaceSurfaceRenderer()
	{
		this.render = new TextureSliceRender();
		this.surfaceRectangle = new Rect2d();
		this.spriteRenderOffset = new Point3d();
		this.spriteSurfaceTranslation = new Vector3d();
		this.spriteFacingVector = new Vector3d();
		this.facing = EnumFacing.NORTH;
		this.lift = 1D / 256D;
	}

	public void setSprite(TextureSlice sprite)
	{
		this.render.setTexture(sprite);
	}

	public void setFacing(EnumFacing facing)
	{
		if(facing == this.facing)
			return;
		this.facing = facing;
		this.updateSpriteRenderOffset();
	}

	public void setLift(double lift)
	{
		if(lift == this.lift)
			return;
		this.lift = lift;
		this.updateSpriteRenderOffset();
	}

	public void setSurfaceRectangle(double x, double y, double w, double h)
	{
		this.surfaceRectangle.setUsingTopLeft(x, y, w, h);
		this.render.setSize(this.surfaceRectangle.width(), this.surfaceRectangle.height());
		this.updateSpriteRenderOffset();
	}

	public void setSurfaceRectangle(Rect2d rectangle)
	{
		this.surfaceRectangle.set(rectangle);
		this.render.setSize(this.surfaceRectangle.width(), this.surfaceRectangle.height());
		this.updateSpriteRenderOffset();
	}

	public void setSurfaceRectangle(Tuple2d offset, Tuple2d size)
	{
		this.setSurfaceRectangle(offset.x, offset.y, size.x, size.y);
	}

	private void updateSpriteHeadingVector()
	{
		Vec3i dir = this.facing.getDirectionVec();
		this.spriteFacingVector.set(dir.getX(), dir.getY(), dir.getZ());
		this.render.setRotationVector(this.spriteFacingVector);
	}

	private void updateSpriteSurfaceTranslation()
	{
		switch(this.facing)
		{
		case UP:
			this.spriteSurfaceTranslation.x = this.surfaceRectangle.centerX();
			this.spriteSurfaceTranslation.y = this.lift;
			this.spriteSurfaceTranslation.z = this.surfaceRectangle.centerY();
			break;
		case DOWN:
			this.spriteSurfaceTranslation.x = -this.surfaceRectangle.centerX();
			this.spriteSurfaceTranslation.y = -this.lift;
			this.spriteSurfaceTranslation.z = -this.surfaceRectangle.centerY();
			break;
		case SOUTH:
			this.spriteSurfaceTranslation.x = this.surfaceRectangle.centerX();
			this.spriteSurfaceTranslation.y = -this.surfaceRectangle.centerY();
			this.spriteSurfaceTranslation.z = this.lift;
			break;
		case NORTH:
			this.spriteSurfaceTranslation.x = -this.surfaceRectangle.centerX();
			this.spriteSurfaceTranslation.y = -this.surfaceRectangle.centerY();
			this.spriteSurfaceTranslation.z = -this.lift;
			break;
		case EAST:
			this.spriteSurfaceTranslation.x = this.lift;
			this.spriteSurfaceTranslation.y = -this.surfaceRectangle.centerY();
			this.spriteSurfaceTranslation.z = -this.surfaceRectangle.centerX();
			break;
		case WEST:
			this.spriteSurfaceTranslation.x = -this.lift;
			this.spriteSurfaceTranslation.y = -this.surfaceRectangle.centerY();
			this.spriteSurfaceTranslation.z = this.surfaceRectangle.centerX();
			break;
		}
	}

	private void updateSpriteRenderOffset()
	{
		this.updateSpriteHeadingVector();
		this.updateSpriteSurfaceTranslation();

		this.spriteRenderOffset.set(this.spriteFacingVector);
		this.spriteRenderOffset.scale(0.5D);
		this.spriteRenderOffset.add(this.spriteSurfaceTranslation);
		this.spriteRenderOffset.add(SHIFT_TO_CENTER);
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		this.render.doRender(x + this.spriteRenderOffset.x, y + this.spriteRenderOffset.y, z + this.spriteRenderOffset.z, partialTicks);
	}
}
