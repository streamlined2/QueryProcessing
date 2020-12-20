package entity.definition;

import java.util.Optional;
import java.util.function.Function;

/**
 * Holds functional reference to property of an entity
 * @author Serhii Pylypenko
 *
 *@param <T> entity type
 *@param <R> type of property
 *
 */

public class FunctionalProperty<T extends Entity,R> implements Property<R>{
	private final Function<T,R> methodReference;
	
	public FunctionalProperty(final Function<T,R> methodReference){
		this.methodReference=methodReference;
	}
	
	@SuppressWarnings("unchecked") @Override
	public R getValue(final Optional<? extends Entity> entity) {
		return methodReference.apply((T)entity.get());
	}
	
	@Override public int hashCode() {
		return methodReference.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override public boolean equals(final Object o) {
		return (o instanceof FunctionalProperty prop) ? 
				methodReference.equals(prop.methodReference):false;
	}
	
	@Override public String toString() {
		return methodReference.toString();
	}
	
}
