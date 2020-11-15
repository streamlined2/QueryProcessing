package entity.definition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Holds getter reference to property of an entity
 * @author Serhii Pylypenko
 *
 *@param <T> entity type
 *@param <R> type of property
*
 */

public class GetterProperty<T extends Entity, R> implements Property<R> {
	private final Method method;

	public GetterProperty(final Class<T> entityClass,final String methodName) {
		try {
			method=entityClass.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked") @Override
	public R getValue(final Optional<? extends Entity> entity) {
		try {
			return (R) method.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked") @Override 
	public boolean equals(final Object o) {
		return (o instanceof GetterProperty) ? method.equals(((GetterProperty<T,R>)o).method):false;
	}
	
	@Override public int hashCode() {
		return method.hashCode();
	}
	
	@Override public String toString() {
		return method.toString();
	}
	
}
