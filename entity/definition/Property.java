package entity.definition;

import java.util.function.Function;

/**
 * Holds reference to property of an entity
 * @author Serhii Pylypenko
 *
 */

public class Property<T extends Entity> {
	private final Function<T,?> getter;
	
	Property(final Function<T,?> getter){
		this.getter=getter;
	}
	
	Object getValue(final T entity) {
		return getter.apply(entity);
	}
	
}
