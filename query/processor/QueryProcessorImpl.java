package query.processor;

import entity.definition.DataSource;
import query.definition.Entry;
import query.definition.Query;
import query.definition.QueryResult;

/**
 * Query processor implementation
 * @author Serhii Pylypenko
 *
 */
public class QueryProcessorImpl implements QueryProcessor {

	private void checkIfAllDataPresent(final Query query,final DataSource dataSource) throws NoDataException {
		for(final Entry<?> entry:query) {
			if(!dataSource.holdsDataFor(entry.getEntityClass())) 
				throw new NoDataException(entry.getEntityClass(),dataSource);
		}
	}
	
	@Override
	public QueryResult fetch(final Query query,final DataSource dataSource) throws QueryException {
		final QueryResult result=new QueryResult();
		
		checkIfAllDataPresent(query,dataSource);
		  //for() { result.add(new Tuple()); }
		 
		return result;
	}

}
