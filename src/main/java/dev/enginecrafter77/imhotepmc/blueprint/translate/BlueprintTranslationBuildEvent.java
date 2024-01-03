package dev.enginecrafter77.imhotepmc.blueprint.translate;

import net.minecraftforge.fml.common.eventhandler.Event;

public class BlueprintTranslationBuildEvent extends Event {
	public final DefaultDataVersionTranslator.VersionTranslatorBuilder builder;

	public BlueprintTranslationBuildEvent(int versionTo)
	{
		this.builder = new DefaultDataVersionTranslator.VersionTranslatorBuilder(versionTo);
	}

	public DefaultDataVersionTranslator.VersionTranslatorBuilder getBuilder()
	{
		return this.builder;
	}
}
