package dev.enginecrafter77.imhotepmc.util.math;

import javax.vecmath.Tuple2d;

public class Rect2d {
	public double left;
	public double top;
	public double right;
	public double bottom;

	public Rect2d()
	{
		this.left = 0D;
		this.top = 0D;
		this.right = 0D;
		this.bottom = 0D;
	}

	public Rect2d(double left, double top, double right, double bottom)
	{
		this.set(left, top, right, bottom);
	}

	public Rect2d(Rect2d other)
	{
		this.set(other);
	}

	public double width()
	{
		return this.right - this.left;
	}

	public double height()
	{
		return this.bottom - this.top;
	}

	public double centerX()
	{
		return (this.left + this.right) / 2D;
	}

	public double centerY()
	{
		return (this.top + this.bottom) / 2D;
	}

	public void center(Tuple2d out)
	{
		out.set(this.centerX(), this.centerY());
	}

	public void set(double left, double top, double right, double bottom)
	{
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public void set(Rect2d other)
	{
		this.left = other.left;
		this.top = other.top;
		this.right = other.right;
		this.bottom = other.bottom;
	}

	public void setUsingTopLeft(double left, double top, double width, double height)
	{
		this.set(left, top, left + width, top + height);
	}

	public void setTopLeftSize(double width, double height)
	{
		this.right = this.left + width;
		this.bottom = this.top + height;
	}

	public void setCenterSize(double width, double height)
	{
		double cx = (this.left + this.right) / 2D;
		double cy = (this.top + this.bottom) / 2D;
		this.set(cx - (width / 2D), cy - (height / 2D), cx + (width / 2D), cy + (height / 2D));
	}

	public void scaleTopLeft(double x, double y)
	{
		this.setTopLeftSize(this.width() * x, this.height() * y);
	}

	public void scaleUniformTopLeft(double scale)
	{
		this.scaleTopLeft(scale, scale);
	}

	public void scaleCenter(double x, double y)
	{
		this.setCenterSize(this.width() * x, this.height() * y);
	}

	public void scaleUniformCenter(double scale)
	{
		this.scaleCenter(scale, scale);
	}

	public void translate(double x, double y)
	{
		this.left += x;
		this.top += y;
		this.right += x;
		this.bottom += y;
	}

	public void moveTopLeftTo(double x, double y)
	{
		this.setUsingTopLeft(x, y, this.width(), this.height());
	}
}
