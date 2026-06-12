package dev.enginecrafter77.imhotepmc.util;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

/**
 * A somewhat rustic class designed to loosely resemble Rust's Result structure.
 * @param <T> The type of the internal value
 */
public class Result<T> {
	@Nullable
	private final T value;

	@Nullable
	private final Throwable error;

	private Result(@Nullable T value, @Nullable Throwable error)
	{
		this.value = value;
		this.error = error;
	}

	public Optional<Throwable> err()
	{
		return Optional.ofNullable(this.error);
	}

	public Optional<T> ok()
	{
		return Optional.ofNullable(this.value);
	}

	public boolean isOk()
	{
		return this.value != null;
	}

	public boolean isErr()
	{
		return this.error != null;
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public T unwrap()
	{
		return this.ok().get();
	}

	public T unwrapOr(T def)
	{
		return this.ok().orElse(def);
	}

	public <O> Result<O> map(Function<T, O> mapper)
	{
		if(this.isErr())
			return new Result<O>(null, this.error);
		return new Result<>(mapper.apply(this.value), null);
	}

	public Result<T> mapErr(Function<Throwable, Throwable> errorMapper)
	{
		if(this.isErr())
			return new Result<T>(null, errorMapper.apply(this.error));
		return new Result<>(this.value, null);
	}

	public static <T> Result<T> ok(T value)
	{
		return new Result<T>(value, null);
	}

	public static <T> Result<T> err(Throwable error)
	{
		return new Result<T>(null, error);
	}
}
