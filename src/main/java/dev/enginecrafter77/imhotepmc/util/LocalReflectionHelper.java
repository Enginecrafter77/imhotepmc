package dev.enginecrafter77.imhotepmc.util;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LocalReflectionHelper {
	private static final Log LOGGER = LogFactory.getLog(LocalReflectionHelper.class);

	public static Field findField(Class<?> clazz, String fieldName, String fieldObfName)
	{
		try
		{
			return ObfuscationReflectionHelper.findField(clazz, fieldObfName);
		}
		catch(ReflectionHelper.UnableToFindFieldException exc)
		{
			LOGGER.error(String.format("Unable to find field %s/%s in %s, using fallback mechanism", fieldName, fieldObfName, clazz.getName()));
			return ReflectionHelper.findField(clazz, fieldName, null); // Fall back to using the dev name
		}
	}

	public static Method findMethod(Class<?> clazz, String methodName, String methodObfName, Class<?> returnType, Class<?>... parameters)
	{
		try
		{
			return ObfuscationReflectionHelper.findMethod(clazz, methodObfName, returnType, parameters);
		}
		catch(ReflectionHelper.UnableToFindMethodException exc)
		{
			LOGGER.error(String.format("Unable to find method %s/%s in %s, using fallback mechanism", methodName, methodObfName, clazz.getName()));
			return ReflectionHelper.findMethod(clazz, methodName, null, parameters); // Fall back to using the dev name
		}
	}

	public static <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>... parameters)
	{
		return ObfuscationReflectionHelper.findConstructor(clazz, parameters);
	}
}
