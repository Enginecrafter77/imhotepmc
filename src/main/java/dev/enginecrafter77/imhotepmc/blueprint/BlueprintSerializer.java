package dev.enginecrafter77.imhotepmc.blueprint;

public interface BlueprintSerializer<T> {
	public T serializeBlueprint(SchematicBlueprint blueprint);
	public SchematicBlueprint deserializeBlueprint(T source);
	public SchematicMetadata deserializeBlueprintMetadata(T source);
}
