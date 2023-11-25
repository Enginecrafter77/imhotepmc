package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.EnumFacing;

import javax.vecmath.Tuple3d;

public enum Axis3d {
	X,
	Y,
	Z;

	public double getCoordinationFrom(Tuple3d tuple)
	{
		switch(this)
		{
		case X:
			return tuple.x;
		case Y:
			return tuple.y;
		case Z:
			return tuple.z;
		default:
			throw new IllegalStateException();
		}
	}

	public void setCoordinationIn(Tuple3d tuple, double value)
	{
		switch(this)
		{
		case X:
			tuple.x = value;
			break;
		case Y:
			tuple.y = value;
			break;
		case Z:
			tuple.z = value;
			break;
		default:
			throw new IllegalStateException();
		}
	}

	public void addToCoordinationIn(Tuple3d tuple, double value)
	{
		value += this.getCoordinationFrom(tuple);
		this.setCoordinationIn(tuple, value);
	}

	public void scaleCoordinationIn(Tuple3d tuple, double value)
	{
		value *= this.getCoordinationFrom(tuple);
		this.setCoordinationIn(tuple, value);
	}

	public static Axis3d maxAxialValueIn(Tuple3d tuple)
	{
		Axis3d maxA = Axis3d.X;
		double maxV = tuple.x;

		if(tuple.y > maxV)
		{
			maxA = Axis3d.Y;
			maxV = tuple.y;
		}

		if(tuple.z > maxV)
		{
			maxA = Axis3d.Z;
			maxV = tuple.z;
		}

		return maxA;
	}

	public static Axis3d fromFacing(EnumFacing facing)
	{
		switch(facing.getAxis())
		{
		case X:
			return X;
		case Y:
			return Y;
		case Z:
			return Z;
		}
		throw new UnsupportedOperationException();
	}
}
