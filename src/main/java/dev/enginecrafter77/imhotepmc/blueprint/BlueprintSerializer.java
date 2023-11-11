package dev.enginecrafter77.imhotepmc.blueprint;

public interface BlueprintSerializer<T> {
	public T serializeBlueprint(SchematicBlueprint blueprint);
	public SchematicBlueprint deserializeBlueprintMetadata(T source);
	public SchematicBlueprint deserializeBlueprint(T source);
}
