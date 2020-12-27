package collections.sort;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * The interface provides access to data container to be sorted
 * 
 * @author Serhii Pylypenko
 * @param <K> type of sequence key
 * @version 1.5
 */
public interface Sequence<K> extends Iterable<K> {
	
	int size();
	K getKey(final int index);
	void swap(final int from,final int to);
	
	default Iterator<K> iterator(){
		return listIterator(0);
	}
	
	default ListIterator<K> listIterator(final int initialIndex) {
		return new ListIterator<K>() {
			private int index=initialIndex;
			
			@Override public boolean hasNext() {
				return index<size();
			}

			@Override public K next() {
				return getKey(index++);
			}

			@Override public int nextIndex() {
				return index;
			}

			@Override public boolean hasPrevious() {
				return index>0;
			}

			@Override public K previous() {
				return getKey(--index);
			}

			@Override public int previousIndex() {
				return index-1;
			}

			@Override public void remove() {
				throw new UnsupportedOperationException("remove operation isn't supported for Sequence interface iterator");
			}

			@Override public void set(K e) {
				throw new UnsupportedOperationException("set operation isn't supported for Sequence interface iterator");
			}

			@Override public void add(K e) {
				throw new UnsupportedOperationException("add operation isn't supported for Sequence interface iterator");
			}
		};
	}
	
}
