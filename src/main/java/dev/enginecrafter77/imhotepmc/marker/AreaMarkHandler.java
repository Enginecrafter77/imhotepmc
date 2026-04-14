package dev.enginecrafter77.imhotepmc.marker;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * AreaMarkHandler describes an object keeping track of {@link MarkedArea} and {@link MarkingAnchor} instances.
 */
public interface AreaMarkHandler extends INBTSerializable<NBTTagCompound> {
	/** @return A list of all active marked areas */
	public Collection<? extends MarkedArea> allAreas();

	/**
	 * Searches for an area with the given ID.
	 * @param id The ID of the area
	 * @return An area with the given ID, or null if no such area exists.
	 */
	@Nullable
	public MarkedArea getArea(UUID id);

	/**
	 * Attempts to locate an anchor at the given position.
	 * @param pos The position of the anchor
	 * @return A {@link MarkingAnchor} instance at the position, or null if there is no anchor at the given position.
	 */
	@Nullable
	public MarkingAnchor getAnchorAt(BlockPos pos);

	/**
	 * Attempts to connect 2 anchors. The contract of this method is as follows:
	 * <ul>
	 *     <li>If both anchors are NOT part of any area, a new area including them both is created.</li>
	 *     <li>
	 *         If one of the anchors is part of an existing area, an attempt is made to add the other one to the area
	 *         <ul>
	 *             <li>If the block cannot be connected to any of the area's anchors, {@link AreaExpandResult#NO_CONNECTING_AXIS} is returned.</li>
	 *             <li>If the block would not expand the area in any axis, {@link AreaExpandResult#NO_EXPANDING_AXIS} is returned.</li>
	 *         </ul>
	 *     </li>
	 *     <li>If both the anchors are part of different areas, {@link AreaExpandResult#CONFLICT} is returned.</li>
	 *     <li>If both the anchors are part of the same area, {@link AreaExpandResult#ALREADY_ADDED} is returned.</li>
	 * </ul>
	 * If the operation succeeded, {@link AreaExpandResult#SUCCESS} is returned.
	 * @param actor The actor connecting the anchors
	 * @param first The first anchor
	 * @param second The second anchor
	 * @param simulate If true, the anchors won't be connected, and only the appropriate result will be returned
	 * @return A {@link AreaExpandResult} type, see above for explanation.
	 */
	public AreaExpandResult connect(AreaMarkingActor actor, MarkingAnchor first, MarkingAnchor second, boolean simulate);

	/**
	 * Attempts to disconnect the given anchor from its area, if it is in one.
	 * If the anchor is not associated with any area, the method does nothing.
	 * @param marker The anchor to disconnect
	 */
	public void disconnect(MarkingAnchor marker);

	/**
	 * Attempts to dismantle an area with the given ID.
	 * Dismantling means unlinking all the anchors associated with the area,
	 * and calling {@link MarkingAnchor#dismantle()} method for each of the anchors.
	 * It is important that before any of the anchors is dismantled, each one of them must be unlinked (i.e. {@link MarkingAnchor#setAreaId(UUID)} called with null parameter).
	 * @param id The ID of the area to dismantle.
	 * @return True if the area was dismantled successfully, false otherwise.
	 */
	public boolean dismantle(UUID id);

	/**
	 * A shortcut method, which searches for an anchor at the given position, and if
	 * one is found, attempts to find the area associated with the anchor.
	 * @param pos The position of the anchor
	 * @return A {@link MarkedArea} instance, or null if there is no anchor at the given position or the anchor is not associated with any area.
	 */
	@Nullable
	public default MarkedArea getAreaAnchoredAt(BlockPos pos)
	{
		MarkingAnchor marker = this.getAnchorAt(pos);
		if(marker == null)
			return null;
		UUID id = marker.getAreaId();
		if(id == null)
			return null;
		return this.getArea(id);
	}
}
