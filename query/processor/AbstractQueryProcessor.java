package query.processor;

import java.io.ObjectStreamField;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import collections.Searcher;
import entity.definition.DataSource;
import entity.definition.Entity;
import entity.definition.EntityInspector;
import entity.definition.EntityInspector.EntityDefinition;
import query.definition.Entry;
import query.definition.Link;
import query.definition.QualifiedProperty;
import query.definition.Query;
import query.exceptions.NoInitialEntityException;
import query.exceptions.NoRelationException;
import query.exceptions.QueryException;

/**
 * Abstract query processor implementation which keeps query data and builds list of relations between entities
 * @author Serhii Pylypenko
 *
 */
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
		while(!nodeSet.isEmpty()) {//fetch fragment of nodes and prepend it to resulting list of nodes 'relations' until 'nodeSet' is not empty 
			mergeFragment(
					nodeSet,getNextFragment(nodeSet));
		}
	}
	
	//retrieve arbitrary node from 'nodeSet' and form chain of sequential nodes as new fragment
	private Deque<Link<? extends Entity,? extends Entity>> getNextFragment(
			final Set<Entry<? extends Entity>> nodeSet) throws QueryException {
		
		final Deque<Link<? extends Entity,? extends Entity>> fragment=new LinkedList<>();
		
		final var i=nodeSet.iterator();
		Entry<? extends Entity> firstNode;
		Optional<Link<? extends Entity,? extends Entity>> link;
		do {//skip nodes that do not refer to another node
			firstNode=i.next();
			link=selectRelation(nodeSet, firstNode);
		}while(!link.isPresent() && i.hasNext());
		
		while(link.isPresent()) {
			fragment.add(link.get());
			nodeSet.remove(firstNode);
			firstNode=link.get().getDestination();
			link=selectRelation(nodeSet, firstNode);
		}
	
		if(nodeSet.size()==1) {//last node
			fragment.add(new Link<>(firstNode)); 
			nodeSet.remove(firstNode);
		}

		return fragment;
	}
	
	//build new list of links by prepending items of 'fragment' from start to previously accumulated nodes 'relations'
	private void mergeFragment(
			final Set<Entry<? extends Entity>> nodeSet,
			final Deque<Link<? extends Entity,? extends Entity>> fragment) throws QueryException 
	{
		if(relations.isEmpty()) {
			prependFragment(fragment);
		}else {
			final var relation=
					selectRelation(
							Collections.singleton(relations.getFirst().getSource()),
							fragment.getLast().getSource());//get relation from last node of 'fragment' to first node of 'relations' if not empty
			if(relation.isPresent()) {//if found relation points at first node of 'relations'
				prependFragment(fragment);
			}else {
				throw new NoRelationException(fragment.getLast(), relations.getFirst());//no relation can be established between last node of 'fragment' and first node of 'relations'
			}
		}
	}
	
	//fetch nodes from tail of 'fragment' and prepend them to 'relations'
	private void prependFragment(final Deque<Link<? extends Entity,? extends Entity>> fragment) {
		while(!fragment.isEmpty()) {
			relations.addFirst(fragment.removeLast());
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
			final Entry<? extends Entity> source) {
		
		final var link=query.getLink(source);//Optional<Link<? extends Entity,? extends Entity>>
		if(link.isPresent()) {//found relation in Query.joints
			return Optional.of(link.get());
		}else {
			final Set<ObjectStreamField> entityReferences=EntityInspector.getEntityRelations(//fetch list of relations by inspecting 'source'
					source.getEntityClass(), 
					Entity.class);
			for(final ObjectStreamField entityRef:entityReferences) {
				final var node=Searcher.linearSearch(//get first relation that points to any unresolved node in 'nodeSet' //Optional<Entry<? extends Entity>>
						nodeSet, entityRef.getType(), 
						(Entry<? extends Entity> entry,Class<?> entityType)->entry.getEntityClass().isAssignableFrom(entityType));
				if(node.isPresent()) {
					return Optional.of(//return first link pointing at still unresolved node from 'nodeSet'
							new Link<>(
									node.get(),
									new QualifiedProperty<>(source, entityRef.getName())));
				}
			}
			return Optional.empty();//no relation found from 'source' to any of 'nodeSet' entries
		}
	}
	
}
