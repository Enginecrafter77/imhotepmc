package dev.enginecrafter77.imhotepmc.container;

import net.minecraft.inventory.Container;

public interface SlotGenerator {
	public void generate(Container container, int x, int y);

	public default SlotGenerator and(SlotGenerator other)
	{
		return (Container container, int x, int y) -> {
			this.generate(container, x, y);
			other.generate(container, x, y);
		};
	}
}
