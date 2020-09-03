package query.definition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Represents result of query processing
 * @author Serhii Pylypenko
 *
 */

public class QueryResult implements Iterable<Tuple> {
	private final List<Tuple> tuples=new LinkedList<>();
	
	void add(final Tuple e) {
		tuples.add(e);
	}
	
	@Override
	public Iterator<Tuple> iterator() {
		return tuples.iterator();
	}
	
	@Override public String toString() {
		final StringJoiner joiner=new StringJoiner("\n");
		forEach(x->joiner.add(x.toString()));
		return joiner.toString();
	}

}
