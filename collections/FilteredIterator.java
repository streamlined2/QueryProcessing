package collections;

import java.util.Iterator;
import java.util.function.BiPredicate;

public class FilteredIterator<T,E> implements IndexedIterator<T> {

	private final Iterator<T> iterator;
	private final E value;
	private final BiPredicate<T, E> check;
	
	public FilteredIterator(final Iterator<T> iterator,final BiPredicate<T,E> check,final E value) {
		this.iterator=iterator;
		this.check=check;
		this.value=value;
	}
	
	private T stash=null;
	private int nextIndex=-1;
	
	private T findNext() {
		while(iterator.hasNext()) {
			var nextOne=iterator.next();
			nextIndex++;
			if(check.test(nextOne, value)) return nextOne;
		}
		return null;
	}
	
	public final int nextIndex() {
		return nextIndex;
	}
	
	@Override
	public boolean hasNext() {
		return (stash=findNext())!=null;
	}
	
	@Override
	public T next() {
		return stash;
	}
	
}
