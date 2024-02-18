package dev.enginecrafter77.imhotepmc.blueprint.builder;

public interface BuilderTaskExecutor {
	public void submit(BuilderTask task);
	public boolean isReady();
	public void cancelAll();
}
