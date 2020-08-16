package query.definition;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

import entity.definition.Entity;

class PredicateList<T extends Entity> implements Iterable<Predicate<T>>{
	private final List<Predicate<T>> predicates=new LinkedList<>();
	
	void addPredicate(final Predicate<T> p) { predicates.add(p);}

	void addAll(final Collection<Predicate<T>> addList) {
		predicates.addAll(addList);//addList.forEach(x->predicates.add(x));
	}

	@Override
	public Iterator<Predicate<T>> iterator() {
		return predicates.iterator();
	}
	
	int size() {
		return predicates.size();
	}
	
	@Override
	public String toString() {
		StringJoiner joiner=new StringJoiner(" and ");
		predicates.forEach((Predicate<T> x)->joiner.add(x.toString()));
		return joiner.toString();
	}
}
