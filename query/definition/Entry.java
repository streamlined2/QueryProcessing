package query.definition;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.Predicate;

import entity.definition.Entity;
import entity.definition.Source;

public class Entry<T extends Entity> {
	private final Source<T> source;
	private final PredicateList<T> predicates;
	
	public Entry(final Source<T> source){
		this.source=source;
		predicates=new PredicateList<T>();
	}
	
	public Class<T> getEntityClass(){
		return source.getEntityClass();
	}
	
	int getPredicateCount() {
		return predicates.size();
	}
	
	void join(final Entry<? extends Entity> dest) {
		//TODO register link between two entries
	}
	
	@SafeVarargs
	final void where(final Predicate<T>...addPredicates) {
		predicates.addAll(Arrays.asList(addPredicates));
	}
			
	boolean validate(final T entity) {
		boolean valid=true;
		for(final Predicate<T> p:predicates) {
			valid=valid && p.test(entity);
		}
		return valid;
	}
	
	String predicateClause() {
		final StringJoiner joiner=new StringJoiner(" and ");
		predicates.forEach((Predicate<T> x)->joiner.add(x.toString()));
		return joiner.toString();
	}
	
	@Override public int hashCode() {
		return source.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override public boolean equals(final Object o) {
		return (o instanceof Entry)?
				source.equals(((Entry<T>)o).source):false;
	}
	
	@Override public String toString() {
		return source.getEntityClass().getSimpleName();
	}
}
