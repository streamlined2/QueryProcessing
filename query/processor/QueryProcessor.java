package query.processor;

import query.definition.Query;
import entity.definition.DataSource;
import query.definition.Entry;
import query.definition.QueryResult;
import query.exceptions.NoDataException;
import query.exceptions.QueryException;

/**
 * Query processor interface
 * @author Serhii Pylypenko
 *
 */

public interface QueryProcessor {
	
	public Query getQuery();
	
	public QueryResult fetch(final DataSource dataSource) throws QueryException;
	
	public default void checkIfAllNecessaryDataSupplied(final DataSource dataSource) throws NoDataException {
		for(final Entry<?> entry:getQuery()) {
			if(!dataSource.hasDataFor(entry)) 
				throw new NoDataException(entry.getEntityClass(),dataSource);
		}
	}
	
}
