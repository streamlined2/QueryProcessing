package query.processor;

import query.definition.QueryResult;

/**
 * Query processor implementation
 * @author Serhii Pylypenko
 *
 */
public class QueryProcessorImpl implements QueryProcessor {

	@Override
	public QueryResult fetch() throws QueryException {
		final QueryResult result=new QueryResult();
		/*
		 * for() { result.add(new Tuple()); }
		 */
		return result;
	}

}
