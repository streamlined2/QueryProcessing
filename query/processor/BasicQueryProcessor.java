package query.processor;

import java.util.Optional;

import entity.definition.DataSource;
import entity.definition.Entity;
import query.definition.Query;
import query.definition.QueryResult;
import query.exceptions.NoInitialEntityException;
import query.exceptions.QueryException;

/**
 * Query processor implementation
 * @author Serhii Pylypenko
 *
 */
public class BasicQueryProcessor extends AbstractQueryProcessor {
	
	public BasicQueryProcessor(final Query query) throws QueryException {
		super(query);
	}

	@Override
	public QueryResult fetch(final DataSource dataSource) throws QueryException {
		
		checkIfAllNecessaryDataSupplied(dataSource);
		
		final QueryResult result=new QueryResult();

		//find entitySource for initial entity of relation list
		final var initialEntitySource=dataSource.getDataFor(
														getInitialLink().getSource());

		if(initialEntitySource.isPresent()) {
			for(var initialTupleEntity:initialEntitySource.get()) {
				final Optional<? extends Entity> iTEntity=Optional.ofNullable(initialTupleEntity);
				if(isTupleAcceptable(iTEntity,dataSource)) {
					result.add(collectTupleData(iTEntity,dataSource));
				}
			}
		}
		else throw new NoInitialEntityException();
		 
		return result;
	}

}
