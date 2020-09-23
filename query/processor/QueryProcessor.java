package query.processor;

import java.io.ObjectStreamField;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

import query.definition.Query;
import entity.definition.DataSource;
import entity.definition.Entity;
import query.definition.Entry;
import query.definition.Link;
import query.definition.QualifiedProperty;
import query.definition.QueryResult;
import utils.Utilities;

/**
 * Query processor interface
 * @author Serhii Pylypenko
 *
 */

public interface QueryProcessor {
	
	public QueryResult fetch(final Query query,final DataSource dataSource) throws QueryException;
	
	public default void checkIfAllNecessaryDataSupplied(final Query query,final DataSource dataSource) throws NoDataException {
		for(final Entry<?> entry:query) {
			if(!dataSource.holdsDataFor(entry.getEntityClass())) 
				throw new NoDataException(entry.getEntityClass(),dataSource);
		}
	}
	
	//build list of nodes from master to slave, last one doesn't refer to any
	public default Deque<Link<? extends Entity,? extends Entity>> buildRelations(final Query query) throws QueryException {
		final Deque<Link<? extends Entity,? extends Entity>> links=new LinkedList<>();
		final Set<Entry<? extends Entity>> nodeSet=query.getEntries();
		while(!nodeSet.isEmpty()) {//process list of entries until it contains no nodes
			prependFragment(
					query,nodeSet,getNextFragment(nodeSet,query),links);
		}
		return links;
	}
	
	//form chain of nodes from 'nodeSet' as new fragment
	private Deque<Link<? extends Entity,? extends Entity>> getNextFragment(
			final Set<Entry<? extends Entity>> nodeSet,final Query query) throws QueryException {
		
		final Deque<Link<? extends Entity,? extends Entity>> fragment=new LinkedList<>();
		do {
			final Entry<? extends Entity> firstNode=nodeSet.iterator().next();
			nodeSet.remove(firstNode);
			final Optional<Link<? extends Entity,? extends Entity>> link=selectRelation(query, nodeSet, firstNode);
			if(!link.isEmpty()) {
				fragment.add(link.get());
			}else {
				break;
			}
		}while(!nodeSet.isEmpty());
		return fragment;
	}
	
	//build new list of links by prepending items of fragment from start
	private void prependFragment(
			final Query query,
			final Set<Entry<? extends Entity>> nodeSet,
			final Deque<Link<? extends Entity,? extends Entity>> fragment,
			final Deque<Link<? extends Entity,? extends Entity>> links) throws QueryException 
	{
		final Optional<Link<? extends Entity,? extends Entity>> relation=
				selectRelation(query,nodeSet,fragment.getLast().getSourceProperty().getEntry());
		if(!relation.isEmpty() && relation.get().pointsAt(links.getFirst().getSourceProperty().getEntry())) {//there is relation between last node of 'fragment' and first node of 'links'
			while(!fragment.isEmpty()) {//fetch every link from 'fragment' and prepend it to 'links'
				links.addFirst(fragment.removeLast());
			}
		}else {
			throw new NoRelationException(fragment.getLast(), links.getFirst());//no relation found between last node of 'fragment' and first node of 'links'
		}
	}

	/**
	 * Checks if given entity types are linked and fetches proper relation either from {@code Query.joints} or by inspecting source entity
	 * @param <E> type of source entity which contains link property
	 * @param <T> type of target entity
	 * @return {@code true} if there is at least one reference property in {@code E} pointing at entity {@code T}
	 */
	public default Optional<Link<? extends Entity,? extends Entity>> selectRelation(
			final Query query,
			final Set<Entry<? extends Entity>> nodeSet,
			final Entry<? extends Entity> source)
	throws QueryException {
		
		final Optional<Link<? extends Entity,? extends Entity>> link=query.getLink(source);
		if(!link.isEmpty()) {//found relation in Query.joints
			return Optional.of(link.get());
		}else {
			final Set<ObjectStreamField> relations=Utilities.getRelations(//fetch list of relations by inspecting 'source'
					source.getEntityClass(), 
					Entity.class);
			for(final ObjectStreamField relation:relations) {
				final Optional<Entry<? extends Entity>> node=Utilities.linearSearch(//get first relation that points to any unresolved node in 'nodeSet'
						nodeSet, relation.getType(), 
						(Entry<? extends Entity> entry)->entry.getEntityClass());
				if(!node.isEmpty()) {
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
	
}
