package query.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import collections.sort.Sequence;

/**
 * Represents result of query processing
 * @author Serhii Pylypenko
 *
 */

public class QueryResult implements Sequence<Comparable<? super Comparable<?>>> {
	private final List<Tuple> tuples=new ArrayList<>();
	
	public void add(final Tuple e) {
		tuples.add(e);
	}
	
	@Override public String toString() {
		final StringJoiner joiner=new StringJoiner("\n");
		tuples.forEach(x->joiner.add(x.toString()));
		return joiner.toString();
	}

	@Override
	public int size() {
		return tuples.size();
	}

	@Override
	public Comparable<? super Comparable<?>> getKey(int index) {
		return tuples.get(index).getOrderKey();
	}

	@Override
	public void swap(int from, int to) {
		final Tuple first=tuples.get(from);
		tuples.set(from, tuples.get(to));
		tuples.set(to, first);
	}

}
