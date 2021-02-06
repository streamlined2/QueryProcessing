package query.definition.result;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;

import query.definition.AggregatedData;
import query.definition.Query;
import query.definition.Tuple;

//preordered query result for aggregation
public class AggregatedQueryResult extends QueryResult {
	
	private final SortedMap<Tuple,AggregatedData> tuples=new TreeMap<>();

	public AggregatedQueryResult(final Query query) {
		super(query);
	}

	//browse list of aggregated attribute properties and combine accumulated earlier data and new portion
	private void combine(final AggregatedData stash,final AggregatedData add) {
		int index=0;
		for(final var i=getQuery().aggregationIterator();i.hasNext();index++) {
			final var property=i.next();
			stash.setValue(index,property.aggregate(stash.getValue(index),add.getValue(index)));
		}
	}
	
	private AggregatedData initial(final AggregatedData data) {
		int index=0;
		final AggregatedData result=new AggregatedData(data.getDimension());
		for(final var i=getQuery().aggregationIterator();i.hasNext();index++) {
			final var property=i.next();
			result.setValue(index,property.map(data.getValue(index)));
		}
		return result;
	}
	
	@Override	public void accumulate(final Tuple tuple,final AggregatedData data) {
		final AggregatedData accumulated=tuples.get(tuple);
		if(accumulated!=null) {
			combine(accumulated,data);//combine & update data
		}else {
			tuples.put(tuple,initial(data));//map initial data and put under key 'tuple'
		}
	}

	@Override	public void finish() {	}//do nothing as this query result is already ordered

	@Override
	public Iterator<Tuple> iterator() {
		return tuples.keySet().iterator();
	}

	@Override public String toString() {
		final StringJoiner joiner=new StringJoiner("\n");
		forEach(tuple->joiner.add(
				new StringBuilder().
				append(tuple.toString()).
				append("=").
				append(tuples.get(tuple).toString())));
		return joiner.toString();
	}

}
