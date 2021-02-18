package query.definition.result;

import query.definition.AggregatedData;
import query.definition.Query;
import query.definition.Tuple;

/**
 * Represents result of query processing
 * @author Serhii Pylypenko
 *
 */

public abstract class QueryResult implements Iterable<Tuple> {
	
	private final Query query;
	
	private static boolean needAggregation(final Query query) {
		return !query.groupByEmpty();
	}
	
	//factory method for proper query result construction depending on query type
	public static QueryResult createQueryResult(final Query query) {
		return needAggregation(query)?
					new AggregatedQueryResult(query):
						new OrdinaryQueryResult(query);
	}
	
	protected QueryResult(final Query query){
		this.query=query;
	}
	
	public Query getQuery() {
		return query;
	}
	
	//process new tuple and save extracted info in query result
	public abstract void accumulate(final Tuple tuple,final AggregatedData data);
	
	//finish construction work to complete collection of tuples
	public abstract void finish();
	
}
