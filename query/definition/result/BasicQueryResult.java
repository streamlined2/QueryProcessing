package query.definition.result;

import query.definition.AggregatedData;
import query.definition.Query;
import query.definition.Tuple;
import query.processor.AbstractQueryProcessor;

/**
 * Represents result of query processing without any support of SQL server
 * @author Serhii Pylypenko
 *
 */

public abstract class BasicQueryResult extends AbstractQueryResult {
	
	protected BasicQueryResult(final Query query,final AbstractQueryProcessor processor){
		super(query,processor);
	}
	
	private static boolean needAggregation(final Query query) {
		return !query.groupByEmpty();
	}
	
	//factory method for proper query result construction depending on query type
	public static BasicQueryResult createQueryResult(final Query query,final AbstractQueryProcessor processor) {
		return needAggregation(query)?
					new AggregatedQueryResult(query,processor):
						new OrdinaryQueryResult(query,processor);
	}
	
	//process new tuple and save extracted info in query result
	public abstract void accumulate(final Tuple tuple,final AggregatedData data);
	
	//finish construction work to complete collection of tuples
	public abstract void finish();
	
}
