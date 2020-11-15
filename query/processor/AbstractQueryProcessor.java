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
import utils.Utilities;

public abstract class AbstractQueryProcessor implements QueryProcessor {

	/**
	 * Builds list of nodes from master to slave, last one doesn't refer to any and no one refers to first node
	 * @param query query instance that holds list of data entries
	 * @return list of data entries sorted in order of found relations
	 * @throws QueryException if relation list cannot be built
	 */
	public Deque<Link<? extends Entity,? extends Entity>> buildListOfRelations(final Query query) throws QueryException {
		final Deque<Link<? extends Entity,? extends Entity>> links=new LinkedList<>();
		final Set<Entry<? extends Entity>> nodeSet=query.getEntries();
		while(!nodeSet.isEmpty()) {//fetch fragment of nodes and prepend it to resulting list of nodes 'links' until 'nodeSet' is not empty 
			prependFragment(
					query,nodeSet,getNextFragment(nodeSet,query),links);
		}
		return links;
	}
	
	//retrieve arbitrary node from 'nodeSet' and form chain of sequential nodes as new fragment
	private Deque<Link<? extends Entity,? extends Entity>> getNextFragment(
			final Set<Entry<? extends Entity>> nodeSet,final Query query) throws QueryException {
		
		final Deque<Link<? extends Entity,? extends Entity>> fragment=new LinkedList<>();
		do {
			final Entry<? extends Entity> firstNode=nodeSet.iterator().next();
			nodeSet.remove(firstNode);
			final Optional<Link<? extends Entity,? extends Entity>> link=selectRelation(query, nodeSet, firstNode);
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
			final Query query,
			final Set<Entry<? extends Entity>> nodeSet,
			final Deque<Link<? extends Entity,? extends Entity>> fragment,
			final Deque<Link<? extends Entity,? extends Entity>> links) throws QueryException 
	{
		final Optional<Link<? extends Entity,? extends Entity>> relation=
				selectRelation(query,nodeSet,fragment.getLast().getSourceProperty().getEntry());//get relation from last node of 'fragment' to some other node
		if(relation.isPresent() && relation.get().pointsAt(links.getFirst().getSourceProperty().getEntry())) {//if found relation points at first node of 'links'
			while(!fragment.isEmpty()) {//fetch nodes from tail of 'fragment' and prepend them to 'links'
				links.addFirst(fragment.removeLast());
			}
		}else {
			throw new NoRelationException(fragment.getLast(), links.getFirst());//no relation can be established between last node of 'fragment' and first node of 'links'
		}
	}

	/**
	 * Checks if given entity {@code source} is linked to another entity and returns found relation either from {@code Query.joints} or by inspecting serializable fields of {@code source}
	 * @param query instance to analyze
	 * @param nodeSet set of candidate nodes to establish relation to
	 * @param source entity that holds relation to another entity
	 * @return instance of {@code Link} wrapped in {@code Optional} which holds reference to linked entity 
	 */
	public Optional<Link<? extends Entity,? extends Entity>> selectRelation(
			final Query query,
			final Set<Entry<? extends Entity>> nodeSet,
			final Entry<? extends Entity> source)
	throws NoRelationException {
		
		final var link=query.getLink(source);//Optional<Link<? extends Entity,? extends Entity>>
		if(link.isPresent()) {//found relation in Query.joints
			return Optional.of(link.get());
		}else {
			final Set<ObjectStreamField> relations=Utilities.getEntityRelations(//fetch list of relations by inspecting 'source'
					source.getEntityClass(), 
					Entity.class);
			for(final ObjectStreamField relation:relations) {
				final Optional<Entry<? extends Entity>> node=Utilities.linearSearch(//get first relation that points to any unresolved node in 'nodeSet'
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
	
	private void enumerateTupleEntities(
			final Optional<? extends Entity> initialTupleEntity,
			final DataSource dataSource,
			final Deque<Link<? extends Entity,? extends Entity>> relations,
			final BiConsumer<Entity,Entry<? extends Entity>> visitor
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
						if(entitySource.get().suitable(tupleEntity.get())) {
							
							visitor.accept(tupleEntity.get(), sourceEntry);
							
							tupleEntity=link.getRelatedEntity(tupleEntity);
	
						}else throw new WrongInitialEntityClassException(entitySource.get(),initialTupleEntity.get());
					}else {
						throw new NoDataException(sourceEntry.getEntityClass(), dataSource);
					}
				}while(relationIterator.hasNext() && tupleEntity.isPresent());
						
			}else throw new NoInitialEntityException();
		}else throw new EmptyEntryListException();
		
	}
	
	private static class AcceptableTupleVisitor<T extends Entity> implements BiConsumer<T,Entry<T>>{
		private boolean acceptable=true;
		@Override
		public void accept(final T entity, final Entry<T> entry) {
			acceptable = acceptable && entry.validate(entity);
		}
		
		boolean isAcceptable() { return acceptable;}
	};
	
	private boolean acceptableTuple(
			final Optional<? extends Entity> initialTuple,
			final DataSource dataSource,
			final Deque<Link<? extends Entity,? extends Entity>> relations) 
	throws QueryException {
		
		final var visitor=new AcceptableTupleVisitor();
		enumerateTupleEntities(initialTuple, dataSource, relations, visitor);
		return visitor.isAcceptable();
	}

}
