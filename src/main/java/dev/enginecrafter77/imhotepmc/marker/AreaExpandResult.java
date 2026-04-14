package dev.enginecrafter77.imhotepmc.marker;

public enum AreaExpandResult {
	/** The block expanded the area */
	SUCCESS,
	/** No axis aligned edge exists between the block and any defining members */
	NO_CONNECTING_AXIS,
	/** The block would not contribute to the area's defined axes */
	NO_EXPANDING_AXIS,
	/** The added block is part of another mark group */
	CONFLICT,
	/** If the added anchor is already part of the area (i.e. the anchors areas match) */
	ALREADY_ADDED
}
