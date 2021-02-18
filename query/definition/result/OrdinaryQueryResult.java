package query.definition.result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

import collections.sort.QuickSorter;
import collections.sort.Sequence;
import query.definition.AggregatedData;
import query.definition.Query;
import query.definition.Tuple;

/**
 * Query result for non-grouped queries which doesn't keep internal order of tuples permanently but gets ordered finally
 * @author Serhii Pylypenko
 *
 */
public class OrdinaryQueryResult extends BasicQueryResult implements Sequence<Tuple> {

	private final List<Tuple> tuples=new ArrayList<>();

	public OrdinaryQueryResult(final Query query) {
		super(query);
	}

	@Override
	public void accumulate(final Tuple tuple,final AggregatedData data) {
		assert data.getDimension()==0: "no aggregated data if GROUP BY clause was not specified";
		tuples.add(tuple);//just append tuple to unordered list, ignore aggregated data
	}

	@Override
	public void finish() {//sort tuple list finally
		new QuickSorter<Tuple>(Comparator.<Tuple>naturalOrder()).sort(this);
	}
	
	@Override
	public int size() {
		return tuples.size();
	}

	@Override
	public Tuple getKey(int index) {
		return tuples.get(index);
	}

	@Override
	public void swap(int from, int to) {
		final Tuple first=tuples.get(from);
		tuples.set(from, tuples.get(to));
		tuples.set(to, first);
	}

	@Override
	public Iterator<Tuple> iterator() {
		return tuples.iterator();
	}

	@Override public String toString() {
		final StringJoiner joiner=new StringJoiner("\n");
		forEach(tuple->joiner.add(tuple.toString()));
		return joiner.toString();
	}

	@Override
	public long getTupleCount() {
		return tuples.size();
	}

}
