package dev.enginecrafter77.imhotepmc.entity;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.util.Axis3d;
import dev.enginecrafter77.imhotepmc.util.Box3d;
import dev.enginecrafter77.imhotepmc.util.SerializableVector3d;
import dev.enginecrafter77.imhotepmc.util.Vec3dSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.vecmath.Vector3d;

public class EntityConstructionTape extends Entity {
	public static final double DISTANCE_PER_TAPE_ITEM = 16D;

	public static final double TAPE_RADIUS = 0.0625; // 1/16 of block

	private static final DataParameter<Vec3d> PAR_ANCHOR_1 = EntityDataManager.createKey(EntityConstructionTape.class, Vec3dSerializer.INSTANCE);
	private static final DataParameter<Vec3d> PAR_ANCHOR_2 = EntityDataManager.createKey(EntityConstructionTape.class, Vec3dSerializer.INSTANCE);

	private final Vector3d a1c, a2c;
	private final Vector3d bbSizeVector;
	private final Vector3d growthVector;
	private final Vector3d posVector;
	private final Box3d boundingBox;
	private double length;
	private Axis3d axis;

	public EntityConstructionTape(World worldIn)
	{
		super(worldIn);

		this.a1c = new Vector3d();
		this.a2c = new Vector3d();
		this.boundingBox = new Box3d();
		this.bbSizeVector = new Vector3d();
		this.growthVector = new Vector3d();
		this.posVector = new Vector3d();
		this.axis = Axis3d.X;
		this.length = 0D;
		this.forceSpawn = true;

		this.setSilent(true);
		this.setNoGravity(true);
	}

	@Override
	protected void entityInit()
	{
		this.getDataManager().register(PAR_ANCHOR_1, Vec3d.ZERO);
		this.getDataManager().register(PAR_ANCHOR_2, Vec3d.ZERO);
	}

	public void setAnchor(Vec3d a1, Vec3d a2)
	{
		this.getDataManager().set(PAR_ANCHOR_1, a1);
		this.getDataManager().set(PAR_ANCHOR_2, a2);
		this.a1c.set(a1.x, a1.y, a1.z);
		this.a2c.set(a2.x, a2.y, a2.z);
		this.updateEntityBounds();
	}

	public Vec3d getFirstAnchor()
	{
		return this.getDataManager().get(PAR_ANCHOR_1);
	}

	public Vec3d getSecondAnchor()
	{
		return this.getDataManager().get(PAR_ANCHOR_2);
	}

	public double getLength()
	{
		return this.length;
	}

	public double getRadius()
	{
		return TAPE_RADIUS;
	}

	public Axis3d getAxis()
	{
		return this.axis;
	}

	protected void updateEntityBounds()
	{
		this.boundingBox.set(this.a1c, this.a2c);
		this.boundingBox.getSize(this.bbSizeVector);
		this.boundingBox.getCenter(this.posVector);

		this.axis = Axis3d.maxAxialValueIn(this.bbSizeVector);
		this.length = this.axis.getCoordinationFrom(this.bbSizeVector);

		double r = this.getRadius();
		this.growthVector.set(r, r, r);
		this.axis.setCoordinationIn(this.growthVector, 0D);

		this.boundingBox.grow(this.growthVector);

		this.posX = this.posVector.x;
		this.posY = this.posVector.y;
		this.posZ = this.posVector.z;

		switch(this.axis)
		{
		case X:
			this.rotationYaw = 0;
			this.rotationPitch = 0;
			break;
		case Y:
			this.rotationYaw = 0;
			this.rotationPitch = 90;
			break;
		case Z:
			this.rotationYaw = 90;
			this.rotationPitch = 0;
			break;
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		Vec3d a1 = this.getFirstAnchor();
		this.a1c.set(a1.x, a1.y, a1.z);

		Vec3d a2 = this.getSecondAnchor();
		this.a2c.set(a2.x, a2.y, a2.z);

		this.updateEntityBounds();
	}

	public void getEntityBoundingBox(Box3d outBox)
	{
		outBox.set(this.boundingBox);
	}

	@Override
	public AxisAlignedBB getEntityBoundingBox()
	{
		return this.boundingBox.toAABB();
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}

	public void destroy()
	{
		this.setDead();
	}

	public void dismantle()
	{
		this.entityDropItem(this.getStoredTapeStack(), 0F);
		this.setDead();
	}

	public ItemStack getStoredTapeStack()
	{
		return getRequiredTapeItemStackForLength(this.getLength());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		SerializableVector3d vec = new SerializableVector3d();

		if(compound.hasKey("start"))
		{
			NBTTagCompound a1t = compound.getCompoundTag("start");
			vec.deserializeNBT(a1t);
			this.getDataManager().set(PAR_ANCHOR_1, vec.toVec3d());
		}

		if(compound.hasKey("end"))
		{
			NBTTagCompound a2t = compound.getCompoundTag("end");
			vec.deserializeNBT(a2t);
			this.getDataManager().set(PAR_ANCHOR_2, vec.toVec3d());
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		SerializableVector3d a1 = new SerializableVector3d();
		SerializableVector3d a2 = new SerializableVector3d();

		a1.set(this.getFirstAnchor());
		a2.set(this.getSecondAnchor());

		compound.setTag("start", a1.serializeNBT());
		compound.setTag("end", a2.serializeNBT());
	}

	public static int getTapeItemsForLength(double length)
	{
		return (int)Math.ceil(length / DISTANCE_PER_TAPE_ITEM);
	}

	public static ItemStack getRequiredTapeItemStackForLength(double length)
	{
		int items = getTapeItemsForLength(length);
		return new ItemStack(ImhotepMod.ITEM_CONSTRUCTION_TAPE, items);
	}

	public static ItemStack getRequiredTapeItemStack(Vec3d start, Vec3d end)
	{
		return getRequiredTapeItemStackForLength(Math.abs(end.length() - start.length()));
	}
}
