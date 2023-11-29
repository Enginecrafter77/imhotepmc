package dev.enginecrafter77.imhotepmc.net;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

@SuppressWarnings("MissingMessageConstructor")
public class ReflectiveHandlerWrapper<T extends IMessage> implements IMessageHandler<T, IMessage> {
	private final Class<T> msgClass;
	private final Class<?> providerClass;
	private final Object instance;
	private final String methodName;

	@Nullable
	private Method method;

	private boolean failed;

	public <C> ReflectiveHandlerWrapper(Class<C> cls, Class<T> msg, C instance, String methodName)
	{
		this.providerClass = cls;
		this.msgClass = msg;
		this.instance = instance;
		this.methodName = methodName;
		this.failed = false;
		this.method = null;
	}

	@Nullable
	protected Method getMethod()
	{
		if(this.failed)
			return null;

		if(this.method == null)
		{
			try
			{
				this.method = this.providerClass.getMethod(this.methodName, new Class<?>[] {this.msgClass, MessageContext.class});
			}
			catch(ReflectiveOperationException exc)
			{
				this.failed = true;
			}
		}
		return this.method;
	}

	@Override
	public IMessage onMessage(T message, MessageContext ctx)
	{
		try
		{
			Method method = this.getMethod();
			if(method == null)
				return null;
			return (IMessage)method.invoke(this.instance, new Object[] {message, ctx});
		}
		catch(ReflectiveOperationException exc)
		{
			throw new RuntimeException(exc);
		}
	}

	public static <T extends IMessage, C> ReflectiveHandlerWrapper<T> create(Class<C> cls, Class<T> msg, C instance, String methodName)
	{
		return new ReflectiveHandlerWrapper<T>(cls, msg, instance, methodName);
	}
}
