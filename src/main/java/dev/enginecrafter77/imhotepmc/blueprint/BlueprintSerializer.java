package dev.enginecrafter77.imhotepmc.blueprint;

public interface BlueprintSerializer<T> {
	public T serializeBlueprint(StructureBlueprint blueprint);
	public StructureBlueprint deserializeBlueprint(T source);
}
