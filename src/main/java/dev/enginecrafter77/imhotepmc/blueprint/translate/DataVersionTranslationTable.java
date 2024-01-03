package dev.enginecrafter77.imhotepmc.blueprint.translate;

import javax.annotation.Nullable;
import java.util.Set;

public interface DataVersionTranslationTable {
	public Set<Integer> getAcceptedDataVersions();
	public int getProducedDataVersion();

	@Nullable
	public DataVersionTranslation getTranslationFor(int version);
}
