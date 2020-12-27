package collections;

import java.util.Iterator;

public interface IndexedIterator<E> extends Iterator<E> {
	
	public int nextIndex();

}
