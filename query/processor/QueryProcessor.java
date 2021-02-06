package query.processor;

import query.definition.Query;
import query.definition.result.QueryResult;
import entity.definition.DataSource;
import query.definition.Entry;
import query.exceptions.NoDataException;
import query.exceptions.OrderGroupClausesNotCompatibleException;
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
	
	public default void checkIfOrderGroupClausesCompatible() throws OrderGroupClausesNotCompatibleException {
		if(!getQuery().groupByEmpty() && !getQuery().aggregatingByOrderProperties()) 
			throw new OrderGroupClausesNotCompatibleException(getQuery());
	}
	
}
