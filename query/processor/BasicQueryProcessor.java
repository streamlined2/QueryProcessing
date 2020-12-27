package query.processor;

import java.util.Comparator;
import java.util.Optional;

import collections.sort.QuickSorter;
import entity.definition.DataSource;
import entity.definition.Entity;
import query.definition.Query;
import query.definition.QueryResult;
import query.definition.Tuple;
import query.exceptions.NoInitialEntityException;
import query.exceptions.QueryException;

/**
 * Basic query processor implementation
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
		
		buildListOfRelations();

		final QueryResult result=new QueryResult();

		//find entitySource for initial entity of relation list
		final var initialEntitySource=dataSource.getDataFor(
														getInitialLink().getSource());

		if(initialEntitySource.isPresent()) {
			for(var initialTupleEntity:initialEntitySource.get()) {
				final Optional<? extends Entity> iTEntity=Optional.ofNullable(initialTupleEntity);
				if(isTupleAcceptable(iTEntity,dataSource)) {
					final Tuple tuple=collectTupleData(iTEntity,dataSource);
					tuple.setOrderKey(composeOrderKey(iTEntity,dataSource));
					result.add(tuple);
				}
			}
			if(!getQuery().sortByEmpty()) {
				new QuickSorter<>(Comparator.naturalOrder()).sort(result);
			}
		}
		else throw new NoInitialEntityException();
		 
		return result;
	}


}
