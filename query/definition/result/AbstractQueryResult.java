package query.definition.result;

import query.definition.Query;
import query.definition.Tuple;
import query.processor.AbstractQueryProcessor;

/**
 * Common abstract ancestor for query results
 * @author Serhii Pylypenko
 *
 */
public abstract class AbstractQueryResult implements Iterable<Tuple> {

	protected final Query query;
	protected final AbstractQueryProcessor processor;
	
	protected AbstractQueryResult(final Query query,final AbstractQueryProcessor processor){
		this.query=query;
		this.processor=processor;
	}
	
	public Query getQuery() {
		return query;
	}
	
	public abstract long getTupleCount();
	
}
