package dev.enginecrafter77.imhotepmc.blueprint.translate;

import net.minecraftforge.fml.common.eventhandler.Event;

public class BlueprintTranslationBuildEvent extends Event {
	public final BlueprintGameVersionTranslator.VersionTranslatorBuilder builder;

	public BlueprintTranslationBuildEvent(int versionTo)
	{
		this.builder = new BlueprintGameVersionTranslator.VersionTranslatorBuilder(versionTo);
	}

	public BlueprintGameVersionTranslator.VersionTranslatorBuilder getBuilder()
	{
		return this.builder;
	}
}
