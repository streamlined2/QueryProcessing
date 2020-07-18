package query.processor;

import query.definition.QueryResult;

/**
 * Query processor interface
 * @author Serhii Pylypenko
 *
 */

public interface QueryProcessor {
	
	public class QueryException extends Exception {
		private static final long serialVersionUID = -5156341231039372847L;
		
	}
	
	QueryResult fetch() throws QueryException;

}
