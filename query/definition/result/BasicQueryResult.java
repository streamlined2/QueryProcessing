package query.definition.result;

import query.definition.AggregatedData;
import query.definition.Query;
import query.definition.Tuple;

/**
 * Represents result of query processing without any support of SQL server
 * @author Serhii Pylypenko
 *
 */

public abstract class BasicQueryResult extends AbstractQueryResult {
	
	protected BasicQueryResult(final Query query){
		super(query);
	}
	
	private static boolean needAggregation(final Query query) {
		return !query.groupByEmpty();
	}
	
	//factory method for proper query result construction depending on query type
	public static BasicQueryResult createQueryResult(final Query query) {
		return needAggregation(query)?
					new AggregatedQueryResult(query):
						new OrdinaryQueryResult(query);
	}
	
	//process new tuple and save extracted info in query result
	public abstract void accumulate(final Tuple tuple,final AggregatedData data);
	
	//finish construction work to complete collection of tuples
	public abstract void finish();
	
}
