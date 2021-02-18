package query.processor;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;

import entity.definition.DataSource;
import entity.definition.Entity;
import query.definition.AggregatedData;
import query.definition.Entry;
import query.definition.Query;
import query.definition.Tuple;
import query.definition.result.BasicQueryResult;
import query.exceptions.EmptyEntryListException;
import query.exceptions.NoDataException;
import query.exceptions.NoInitialEntityException;
import query.exceptions.QueryException;
import query.exceptions.WrongInitialEntityClassException;

/**
 * Query processor that doesn't rely on SQL server, but joins and filters entities, fetches attributes, computes aggregated data and sorts results solely by itself
 * @author Serhii Pylypenko
 *
 */
public class BasicQueryProcessor extends AbstractQueryProcessor {
	
	protected final DataSource dataSource;

	public BasicQueryProcessor(final Query query,final DataSource dataSource) throws QueryException {
		super(query);
		this.dataSource=dataSource;
	}

	@Override
	public BasicQueryResult fetch() throws QueryException {
		
		checkIfAllNecessaryDataSupplied(dataSource);
		checkIfOrderGroupClausesCompatible();
		
		buildListOfRelations();

		final BasicQueryResult result=BasicQueryResult.createQueryResult(getQuery());

		//find entitySource for initial entity of relation list
		final var initialEntitySource=dataSource.getDataFor(
														getInitialLink().getSource());

		if(initialEntitySource.isPresent()) {
			for(var initialTupleEntity:initialEntitySource.get()) {
				final Optional<? extends Entity> iTEntity=Optional.ofNullable(initialTupleEntity);
				if(isTupleAcceptable(iTEntity,dataSource)) {
					final Tuple tuple=collectTupleData(iTEntity,dataSource);
					tuple.setOrderKey(composeOrderKey(iTEntity,dataSource));
					result.accumulate(tuple,aggregateValues(iTEntity,dataSource));
				}
			}
			result.finish();
		}
		else throw new NoInitialEntityException();
		 
		return result;
	}

	//iterate through every entity of query to check and compose tuple data
	private void enumerateTupleEntities(
			final Optional<? extends Entity> initialTupleEntity,
			final DataSource dataSource,
			final BiConsumer<Optional<? extends Entity>,Entry<? extends Entity>> visitor
	) throws QueryException {
		
		final var relationIterator=relations.iterator();//Iterator<Link<? extends Entity,? extends Entity>> 
		if(relationIterator.hasNext()) {
			
			if(initialTupleEntity.isPresent()) {
				var tupleEntity=initialTupleEntity;
	
				do {
					final var link=relationIterator.next();////get next relation link //Link<? extends Entity,? extends Entity>
					final var sourceEntry=link.getSource();
					final var entitySource=dataSource.getDataFor(sourceEntry);//Optional<EntitySource<? extends Entity>>
					
					if(entitySource.isPresent()) {
						if(entitySource.get().suitable(tupleEntity)) {
							
							visitor.accept(tupleEntity, sourceEntry);
							
							tupleEntity=link.getRelatedEntity(tupleEntity);
	
						}else throw new WrongInitialEntityClassException(entitySource,initialTupleEntity);
					}else {
						throw new NoDataException(sourceEntry, dataSource);
					}
				}while(relationIterator.hasNext() && tupleEntity.isPresent());
						
			}else throw new NoInitialEntityException();
		}else throw new EmptyEntryListException();
		
	}
	
	//visits every tuple entity to check if given tuple is acceptable to be included in resulting query 
	private class AcceptableTupleVisitor<T extends Entity> implements BiConsumer<Optional<T>,Entry<T>>{
		private boolean acceptable=true;
		
		@Override public void accept(final Optional<T> entity, final Entry<T> entry) {
			acceptable = acceptable && entry.validate(entity.get());
		}
		
		boolean isAcceptable() { return acceptable;}
	};
	
	//constructs visitor and passes it to visit every tuple entity and gather info if entity is acceptable for resulting query
	protected boolean isTupleAcceptable(
			final Optional<? extends Entity> initialTupleEntity,
			final DataSource dataSource) 
	throws QueryException {
		
		final var visitor=new AcceptableTupleVisitor();
		enumerateTupleEntities(initialTupleEntity, dataSource, visitor);
		return visitor.isAcceptable();
	}
	
	private class CollectTupleDataVisitor<T extends Entity> implements BiConsumer<Optional<T>,Entry<T>>{
		
		private final Tuple tuple;
		
		CollectTupleDataVisitor(final int valuesDimension){
			tuple=new Tuple(valuesDimension);
		}

		Tuple getTuple() {
			return tuple;
		}
		
		@Override
		//extract values of given entity/entry and collect them in 'tuple'
		public void accept(final Optional<T> entity, final Entry<T> entry) {
			for(var i=getQuery().selectIterator(entry);i.hasNext();) {//scan entity properties for 'entry'
				//find column index for property,evaluate and map value to tuple column
				final var property=i.next();
				tuple.setValue(i.nextIndex(), property.getProperty().getValue(entity));
			};
			
		}
		
	}
	
	//constructs visitor and passes it to visit every tuple entity, evaluate property values and collect data and pack to resulting tuple
	protected Tuple collectTupleData(
			final Optional<? extends Entity> initialTupleEntity,
			final DataSource dataSource) 
	throws QueryException {
		final var visitor=new CollectTupleDataVisitor(getQuery().selectDimension());
		enumerateTupleEntities(initialTupleEntity, dataSource, visitor);
		return visitor.getTuple();
	}
	
	private class ComposeOrderKeyVisitor<T extends Entity> implements BiConsumer<Optional<T>,Entry<T>>{
		
		private final Object keys[];
		
		ComposeOrderKeyVisitor(final int dimension){
			keys=new Object[dimension];
		}

		StringBuilder getOrderKey() {
			final StringBuilder orderKey=new StringBuilder();
			Arrays.asList(keys).forEach(x->orderKey.append(x));
			return orderKey;
		}
		
		@Override
		//extract values of given entity/entry and compose them as sort key
		public void accept(final Optional<T> entity, final Entry<T> entry) {
			for(var i=getQuery().sortGroupByIterator(entry);i.hasNext();) {//scan entity properties for 'entry'
				//evaluate property of entity and store value as part of future sort key
				final var property=i.next();
				keys[i.nextIndex()]=property.getProperty().getValue(entity);
			};
			
		}
		
	}
	
	//constructs visitor and passes it to compose sort key for every tuple
	protected StringBuilder composeOrderKey(
			final Optional<? extends Entity> initialTupleEntity,
			final DataSource dataSource) 
	throws QueryException {
		final var visitor=new ComposeOrderKeyVisitor(getQuery().sortGroupByDimension());
		enumerateTupleEntities(initialTupleEntity, dataSource, visitor);
		return visitor.getOrderKey();
	}
	
	private class AggregateEvaluationVisitor<T extends Entity> implements BiConsumer<Optional<T>,Entry<T>>{
		
		private final AggregatedData data;
		
		AggregateEvaluationVisitor(final int aggregationDimension){
			this.data=new AggregatedData(aggregationDimension);
		}
		
		AggregatedData getData() {
			return data;
		}

		@Override
		//collect and store aggregated values 
		public void accept(final Optional<T> entity, final Entry<T> entry) {
			for(var i=getQuery().aggregationIterator(entry);i.hasNext();) {//scan entity properties for 'entry'
				//evaluate property of entity and store value as part of future aggregation value
				final var property=i.next();
				data.setValue(
						i.nextIndex(), property.getProperty().getValue(entity));
			};
			
		}
		
	}
	
	//constructs visitor to gather aggregate values for every group key
	protected AggregatedData aggregateValues(
			final Optional<? extends Entity> initialTupleEntity,
			final DataSource dataSource) 
	throws QueryException {
		final var visitor = new AggregateEvaluationVisitor(getQuery().aggregateDimension());
		enumerateTupleEntities(
				initialTupleEntity, 
				dataSource, 
				visitor);
		return visitor.getData();
	}

}
