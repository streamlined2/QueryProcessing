package query.processor;

import java.io.ObjectStreamField;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import entity.definition.DataSource;
import entity.definition.Entity;
import query.definition.Entry;
import query.definition.Link;
import query.definition.QualifiedProperty;
import query.definition.Query;
import query.exceptions.EmptyEntryListException;
import query.exceptions.NoDataException;
import query.exceptions.NoInitialEntityException;
import query.exceptions.NoRelationException;
import query.exceptions.QueryException;
import query.exceptions.WrongInitialEntityClassException;
import query.definition.Tuple;
import utils.Utilities;

public abstract class AbstractQueryProcessor implements QueryProcessor {

	protected final Query query;
	protected final Deque<Link<? extends Entity,? extends Entity>> relations=new LinkedList<>();
	
	public AbstractQueryProcessor(final Query query) throws QueryException {
		this.query=query;
	}
	
	@Override public final Query getQuery() {
		return query;
	}
	
	/**
	 * Get first link in relation list
	 * @return initial node of relation list
	 * @throws NoInitialEntityException
	 */
	public Link<? extends Entity,? extends Entity> getInitialLink() throws NoInitialEntityException {
		if(relations.size()>0) return relations.peek();
		else throw new NoInitialEntityException();
	}
	
	/**
	 * Builds list of nodes from master to slave, last one doesn't refer to any and no one refers to first node
	 * @throws QueryException if relation list cannot be built
	 */
	protected void buildListOfRelations() throws QueryException {
		final Set<Entry<? extends Entity>> nodeSet=query.getEntries();
		while(!nodeSet.isEmpty()) {//fetch fragment of nodes and prepend it to resulting list of nodes 'links' until 'nodeSet' is not empty 
			prependFragment(
					nodeSet,getNextFragment(nodeSet));
		}
	}
	
	//retrieve arbitrary node from 'nodeSet' and form chain of sequential nodes as new fragment
	private Deque<Link<? extends Entity,? extends Entity>> getNextFragment(
			final Set<Entry<? extends Entity>> nodeSet) throws QueryException {
		
		final Deque<Link<? extends Entity,? extends Entity>> fragment=new LinkedList<>();
		do {
			final Entry<? extends Entity> firstNode=nodeSet.iterator().next();
			nodeSet.remove(firstNode);
			final Optional<Link<? extends Entity,? extends Entity>> link=selectRelation(nodeSet, firstNode);
			if(link.isPresent()) {
				fragment.add(link.get());
			}else {
				break;//can't find next node, break loop 
			}
		}while(!nodeSet.isEmpty());
		return fragment;
	}
	
	//build new list of links by prepending items of 'fragment' from start to previously accumulated nodes 'links'
	private void prependFragment(
			final Set<Entry<? extends Entity>> nodeSet,
			final Deque<Link<? extends Entity,? extends Entity>> fragment) throws QueryException 
	{
		final Optional<Link<? extends Entity,? extends Entity>> relation=
				selectRelation(nodeSet,fragment.getLast().getSourceProperty().getEntry());//get relation from last node of 'fragment' to some other node
		if(relation.isPresent() && relation.get().pointsAt(relations.getFirst().getSourceProperty().getEntry())) {//if found relation points at first node of 'links'
			while(!fragment.isEmpty()) {//fetch nodes from tail of 'fragment' and prepend them to 'links'
				relations.addFirst(fragment.removeLast());
			}
		}else {
			throw new NoRelationException(fragment.getLast(), relations.getFirst());//no relation can be established between last node of 'fragment' and first node of 'links'
		}
	}

	/**
	 * Checks if given entity {@code source} is linked to another entity and returns found relation either from {@code Query.joints} or by inspecting serializable fields of {@code source}
	 * @param nodeSet set of candidate nodes to establish relation to
	 * @param source entity that holds relation to another entity
	 * @return instance of {@code Link} wrapped in {@code Optional} which holds reference to linked entity 
	 */
	protected Optional<Link<? extends Entity,? extends Entity>> selectRelation(
			final Set<Entry<? extends Entity>> nodeSet,
			final Entry<? extends Entity> source)
	throws NoRelationException {
		
		final var link=query.getLink(source);//Optional<Link<? extends Entity,? extends Entity>>
		if(link.isPresent()) {//found relation in Query.joints
			return Optional.of(link.get());
		}else {
			final Set<ObjectStreamField> entityRelations=Utilities.getEntityRelations(//fetch list of relations by inspecting 'source'
					source.getEntityClass(), 
					Entity.class);
			for(final ObjectStreamField relation:entityRelations) {
				final var node=Utilities.linearSearch(//get first relation that points to any unresolved node in 'nodeSet' //Optional<Entry<? extends Entity>>
						nodeSet, relation.getType(), 
						(Entry<? extends Entity> entry,Class<?> entityType)->entry.getEntityClass().isAssignableFrom(entityType));
				if(node.isPresent()) {
					return Optional.of(//return first link pointing at still unresolved node from 'nodeSet'
							new Link<>(
									node.get(),
									new QualifiedProperty<>(source, relation.getName())));
				}
			}
			//no relation found between 'source' and 'target' entries
			throw new NoRelationException(source);
		}
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
					final var sourceEntry=link.getSourceProperty().getEntry();
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
		
		CollectTupleDataVisitor(final int dimension){
			tuple=new Tuple(dimension);
		}

		public Tuple getTuple() {
			return tuple;
		}
		
		@Override
		//extract values of given entity/entry and collect them in 'tuple'
		public void accept(final Optional<T> entity, final Entry<T> entry) {
			for(var i=getQuery().selectIterator(entry);i.hasNext();) {//scan entity properties for 'entry'
				//find column index for property,evaluate and map value to tuple column
				final var property=i.next();
				tuple.set(i.nextIndex(), property.getProperty().getValue(entity));
			};
			
		}
		
	}
	
	//constructs visitor and passes it to visit every tuple entity, evaluate property values and collect data and pack to resulting tuple
	protected Tuple collectTupleData(
			final Optional<? extends Entity> initialTupleEntity,
			final DataSource dataSource) 
	throws QueryException {
		final var visitor=new CollectTupleDataVisitor(getQuery().dimension());
		enumerateTupleEntities(initialTupleEntity, dataSource, visitor);
		return visitor.getTuple();
	}
	
}
