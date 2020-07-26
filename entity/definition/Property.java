package entity.definition;

import java.util.function.Function;

/**
 * Holds reference to property of an entity
 * @author Serhii Pylypenko
 *
 *@param <T> entity type
 *
 */

public class Property<T extends Entity> {
	private final Function<T,?> getter;
	
	public Property(final Function<T,?> getter){
		this.getter=getter;
	}
	
	public Object getValue(final T entity) {
		return getter.apply(entity);
	}
	
	@Override public int hashCode() {
		return getter.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override public boolean equals(final Object o) {
		return (o instanceof Property) ? getter.equals(((Property<T>)o).getter):false;
	}
	
	@Override public String toString() {
		return getter.toString();
	}
	
}
