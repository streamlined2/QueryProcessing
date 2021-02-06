package collections.sort;

/**
 * The interface provides access to data container to be sorted
 * 
 * @author Serhii Pylypenko
 * @param <K> type of sequence key
 * @version 1.5
 */
public interface Sequence<K> {
	
	int size();
	K getKey(final int index);
	void swap(final int from,final int to);
	
}
