package collections;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Collections and arrays searching utility class 
 * @author Serhii Pylypenko
 *
 */
public final class Searcher {
	
	private Searcher() {}

	/**
	 * Performs linear search within passed {@code array}
	 * @param <T> type of array element
	 * @param <K> type of key to search for
	 * @param array to search
	 * @param key value to search for
	 * @param keyFunc mapping function that turns array element into key
	 * @param startAt start search from index {@code startAt}
	 * @return index of array element if found or {@code array.length} otherwise
	 */
	public static <T,K> Optional<T> linearSearch(final T[] data,final int startAt,final K key,final Function<T, K> keyFunc) {
		for(int k=Math.max(startAt,0);k<data.length;k++) {
			if(Objects.equals(key, keyFunc.apply(data[k]))) return Optional.ofNullable(data[k]);
		}
		return Optional.empty();
	}

	public static <T,K> Optional<T> linearSearch(final Iterable<T> data,final K key,final Function<T, K> keyFunc) {
		return Searcher.linearSearch(data.iterator(),key,keyFunc);
	}

	public static <T,K> Optional<T> linearSearch(final Iterator<T> iterator,final K key,final Function<T,K> keyFunc) {
		for(T value;iterator.hasNext();) {
			value=iterator.next();
			if(Objects.equals(key, keyFunc.apply(value))) return Optional.ofNullable(value);
		}
		return Optional.empty();
	}

	public static <T,K> Optional<T> linearSearch(final Iterable<T> data,final K key,final BiPredicate<T, K> check) {
		return Searcher.linearSearch(data.iterator(),key,check);
	}

	public static <T,K> Optional<T> linearSearch(final Iterator<T> iterator,final K key,final BiPredicate<T,K> check){
		for(T value;iterator.hasNext();) {
			value=iterator.next();
			if(check.test(value,key)) return Optional.ofNullable(value);
		}
		return Optional.empty();
	};

}
