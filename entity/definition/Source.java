package entity.definition;

import java.util.Iterator;

/**
 * Source data class holder
 * @author Serhii Pylypenko
 *
 * @param <T> entity type
 */

public class Source<T extends Entity> implements Iterable<T> {
	
	private final Iterator<T> iterator;

	@SafeVarargs
	public Source(final T... data) {
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
	
	public Source(final Iterator<T> iterator) {
		this.iterator=iterator;
	}

	@Override
	public Iterator<T> iterator() {
		return iterator;
	}
	
}
