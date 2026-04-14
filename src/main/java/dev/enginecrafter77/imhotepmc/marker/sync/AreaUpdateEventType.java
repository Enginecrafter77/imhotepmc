package dev.enginecrafter77.imhotepmc.marker.sync;

public enum AreaUpdateEventType {
	/** Notifies the client that the area was changed */
	UPDATE,
	/** Notifies the client about a new area */
	CREATE,
	/** Notifies the client about an area that was removed */
	REMOVE,
	/** The same as update, but the change will be processed internally */
	SYNC
}
