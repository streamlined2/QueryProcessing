package query.processor;

import query.definition.Query;
import entity.definition.DataSource;
import query.definition.Entry;
import query.definition.QueryResult;

/**
 * Query processor interface
 * @author Serhii Pylypenko
 *
 */

public interface QueryProcessor {
	
	public QueryResult fetch(final Query query,final DataSource dataSource) throws QueryException;
	
	public default void checkIfAllNecessaryDataSupplied(final Query query,final DataSource dataSource) throws NoDataException {
		for(final Entry<?> entry:query) {
			if(!dataSource.hasDataFor(entry)) 
				throw new NoDataException(entry.getEntityClass(),dataSource);
		}
	}
	
}
