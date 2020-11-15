package query.processor;

import entity.definition.DataSource;
import query.definition.Query;
import query.definition.QueryResult;

/**
 * Query processor implementation
 * @author Serhii Pylypenko
 *
 */
public class QueryProcessorImpl extends AbstractQueryProcessor {

	@Override
	public QueryResult fetch(final Query query,final DataSource dataSource) throws QueryException {
		final QueryResult result=new QueryResult();
		
		checkIfAllNecessaryDataSupplied(query,dataSource);
		//for() { result.add(new Tuple()); }
		//TODO implement rest of method
		 
		return result;
	}

}
