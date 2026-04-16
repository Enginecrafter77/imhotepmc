package dev.enginecrafter77.imhotepmc.marker;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class AreaDismantleEvent extends WorldEvent {
	private final MarkedArea area;

	public AreaDismantleEvent(World world, MarkedArea area)
	{
		super(world);
		this.area = area;
	}

	public MarkedArea getArea()
	{
		return this.area;
	}

	public static class Pre extends AreaDismantleEvent
	{
		public Pre(World world, MarkedArea area)
		{
			super(world, area);
		}

		@Override
		public boolean isCancelable()
		{
			return true;
		}
	}

	public static class Post extends AreaDismantleEvent
	{
		private final boolean successful;

		public Post(World world, MarkedArea area, boolean successful)
		{
			super(world, area);
			this.successful = successful;
		}

		public boolean wasSuccessful()
		{
			return this.successful;
		}
	}
}
