package entity.definition;

import java.util.Iterator;

/**
 * Entity data class holder
 * @author Serhii Pylypenko
 *
 * @param <T> entity type
 */

public class EntitySource<T extends Entity> implements Iterable<T> {
	
	private final Iterator<T> iterator;
	private final Class<T> entityClass;

	@SafeVarargs
	public EntitySource(final T... data) {
		entityClass=(Class<T>) data.getClass().getComponentType();
		this.iterator=new Iterator<T>() {
			private int index=0;

			@Override
			public boolean hasNext() {
				return index<data.length;
			}

			@Override
			public T next() {
				return data[index++];
			}			
		};
	}
	
	public EntitySource(final Class<T> cl,final Iterator<T> iterator) {
		entityClass=cl;
		this.iterator=iterator;
	}
	
	public Class<T> getEntityClass(){
		return entityClass;
	}

	@Override
	public Iterator<T> iterator() {
		return iterator;
	}
	
}
