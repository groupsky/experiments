package android.os;

import java.lang.reflect.Method;
import android.content.Context;

public class SystemPropertiesProxy {

	/**
	 * This class cannot be instantiated
	 */
	private SystemPropertiesProxy() {

	}

	/**
	 * Get the value for the given key.
	 * 
	 * @return <code>null</code> if the key isn't found
	 * @throws IllegalArgumentException
	 *             if the key exceeds 32 characters
	 */
	public static String get(Context context, String key)
			throws IllegalArgumentException {
		try {

			ClassLoader cl = context.getClassLoader();
			Class<?> SystemProperties = cl
					.loadClass("android.os.SystemProperties");

			// Parameters Types
			Class<?>[] paramTypes = new Class<?>[1];
			paramTypes[0] = String.class;

			Method get = SystemProperties.getMethod("get", paramTypes);

			// Parameters
			Object[] params = new Object[1];
			params[0] = new String(key);

			return (String) get.invoke(SystemProperties, params);
		} catch (IllegalArgumentException iAE) {
			throw iAE;
		} catch (Exception e) {
			return null;
		}
	}
}