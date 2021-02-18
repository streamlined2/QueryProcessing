package query.definition.result;

import query.definition.Query;
import query.definition.Tuple;

/**
 * Common abstract ancestor for query results
 * @author Serhii Pylypenko
 *
 */
public abstract class AbstractQueryResult implements Iterable<Tuple> {

	protected final Query query;
	
	protected AbstractQueryResult(final Query query){
		this.query=query;
	}
	
	public Query getQuery() {
		return query;
	}
	
	public abstract long getTupleCount();
	
}
