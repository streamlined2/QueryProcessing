package query.definition;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Predicate;

import entity.definition.Entity;
import entity.definition.Property;

public class Entry<T extends Entity> {
	private final Query query;
	private final Class<T> entityClass;
	private final PredicateList<T> predicates;
	private Character alias;
	
	public Entry(final Query query,final Class<T> entityClass,final Character alias){
		this.query=query;
		this.entityClass=entityClass;
		this.alias=alias;
		predicates=new PredicateList<T>();
	}
	
	@SuppressWarnings("unchecked")
	public <R> Entry<T> select(final Function<T,R>... getters) {
		for(final Function<T,R> getter:getters) {
			query.select(new QualifiedProperty<T,R>(this,getter));
		}
		return this;
	}
	
	public Character getAlias() {
		return alias;
	}
	
	public Class<T> getEntityClass(){
		return entityClass;
	}
	
	int getPredicateCount() {
		return predicates.size();
	}
	
	public <R extends Entity> void  joinOn(final Entry<R> dest,final Property<T,R> property) {
		query.join(this,dest,property);
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
		return Objects.hash(entityClass.hashCode(),getAlias());
	}
	
	@SuppressWarnings("unchecked")
	@Override public boolean equals(final Object o) {
		return (o instanceof Entry)?
				entityClass.equals(((Entry<T>)o).entityClass):false;
	}
	
	@Override public String toString() {
		return entityClass.getSimpleName();
	}
}
