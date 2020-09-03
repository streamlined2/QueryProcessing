package entity.definition;

import java.util.function.Function;

/**
 * Holds functional reference to property of an entity
 * @author Serhii Pylypenko
 *
 *@param <T> entity type
 *@param <R> type of property
 *
 */

public class FunctionalProperty<T extends Entity,R> implements Property<T,R>{
	private final Function<T,R> methodReference;
	
	public FunctionalProperty(final Function<T,R> methodReference){
		this.methodReference=methodReference;
	}
	
	@Override
	public R getValue(final T entity) {
		return methodReference.apply(entity);
	}
	
	@Override public int hashCode() {
		return methodReference.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override public boolean equals(final Object o) {
		return (o instanceof FunctionalProperty) ? methodReference.equals(((FunctionalProperty<T,R>)o).methodReference):false;
	}
	
	@Override public String toString() {
		return methodReference.toString();
	}
	
}
