package dev.enginecrafter77.imhotepmc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public abstract class EntityPrimedExplosive extends Entity {
	private static final DataParameter<Integer> PARAMETER_FUSE = EntityDataManager.createKey(EntityPrimedExplosive.class, DataSerializers.VARINT);
	private static final String NBT_KEY_FUSE = "fuse";
	private static final int DEFAULT_FUSE_TIME_TICKS = 80;

	private int fuse;

	public EntityPrimedExplosive(World worldIn)
	{
		super(worldIn);
		this.fuse = DEFAULT_FUSE_TIME_TICKS;
	}

	public abstract void explode();

	public void setFuse(int fuse)
	{
		this.fuse = fuse & 0xFFFF; // trim to short
		this.dataManager.set(PARAMETER_FUSE, this.fuse);
	}

	public int getFuse()
	{
		return this.fuse;
	}

	@Override
	protected void entityInit()
	{
		this.dataManager.register(PARAMETER_FUSE, DEFAULT_FUSE_TIME_TICKS);
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public boolean canBePushed()
	{
		return true;
	}

	@Override
	public void onUpdate()
	{
		EntityUtils.applyBasicPhysics(this);

		--this.fuse;

		if(this.fuse <= 0)
		{
			this.setDead();
			this.explode();
		}
		else
		{
			this.handleWaterMovement();
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		this.dataManager.set(PARAMETER_FUSE, (int)compound.getShort(NBT_KEY_FUSE));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		compound.setShort(NBT_KEY_FUSE, this.dataManager.get(PARAMETER_FUSE).shortValue());
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key)
	{
		super.notifyDataManagerChange(key);
		if(key == PARAMETER_FUSE)
			this.fuse = this.dataManager.get(PARAMETER_FUSE);
	}
}
