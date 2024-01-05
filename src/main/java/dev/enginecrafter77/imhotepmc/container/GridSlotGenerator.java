package dev.enginecrafter77.imhotepmc.container;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.util.Point;
import org.lwjgl.util.WritablePoint;

import java.lang.reflect.Method;

public class GridSlotGenerator implements SlotGenerator {
	private static final Method SLOT_INSERT_METHOD = ObfuscationReflectionHelper.findMethod(Container.class, "func_75146_a", Slot.class, Slot.class);

	private final SlotFactory factory;
	private final SlotIndexer indexer;
	private final int rows;
	private final int cols;

	public GridSlotGenerator(SlotFactory factory, SlotIndexer indexer, int rows, int cols)
	{
		this.indexer = indexer;
		this.factory = factory;
		this.rows = rows;
		this.cols = cols;
	}

	public void generate(Container container, int x, int y)
	{
		Point slotPos = new Point();
		for(int row = 0; row < this.rows; ++row)
		{
			for(int col = 0; col < this.cols; ++col)
			{
				this.calculateSlotPositon(x, y, row, col, slotPos);
				int index = this.indexer.getSlotIndex(this.rows, this.cols, row, col);
				Slot slot = this.factory.createSlot(index, slotPos.getX(), slotPos.getY());
				this.injectSlot(container, slot);
			}
		}
	}

	protected void calculateSlotPositon(int originX, int originY, int row, int col, WritablePoint point)
	{
		point.setX(originX + col * 18);
		point.setY(originY + row * 18);
	}

	protected void injectSlot(Container container, Slot slot)
	{
		try
		{
			SLOT_INSERT_METHOD.invoke(container, slot);
		}
		catch(Exception exc)
		{
			throw new RuntimeException("Injecting slot failed", exc);
		}
	}

	public static interface SlotIndexer
	{
		public static SlotIndexer DEFAULT = horizontal(0);

		public int getSlotIndex(int rows, int cols, int row, int col);

		public static SlotIndexer horizontal(int offset)
		{
			return (int rows, int cols, int row, int col) -> offset + (row * cols + col);
		}
	}

	public static interface SlotFactory
	{
		public Slot createSlot(int index, int x, int y);
	}
}
