package query.definition;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import entity.definition.Entity;

/**
 * Holds list of predicates to filter out entities
 * @author Serhii Pylypenko
 *
 */

class PredicateList implements Iterable<Predicate<Entity>> {
	
	private List<Predicate<Entity>> predicates;
	
	PredicateList(final Predicate<Entity>[] entries){
		for(Predicate<Entity> entry:entries) {
			predicates.add(entry);
		}
	}

	@Override
	public Iterator<Predicate<Entity>> iterator() {
		return predicates.iterator();
	}

}
