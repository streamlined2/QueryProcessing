package query.definition;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Predicate;

import entity.definition.Entity;
import entity.definition.Property;
import math.Numeric;
import query.definition.aggregators.Aggregator;

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
	
	public <R> Entry<T> select(
			@SuppressWarnings("unchecked") final Function<T,R>... getters) {
		for(final Function<T,R> getter:getters) {
			query.select(new QualifiedProperty<T,R>(this,getter));
		}
		return this;
	}
	
	public <R> Entry<T> select(
			@SuppressWarnings("unchecked") final String... getterMethods) {
		for(final String getter:getterMethods) {
			query.select(new QualifiedProperty<T,R>(this,getter));
		}
		return this;
	}
	
	public <R> Entry<T> sortBy(
			@SuppressWarnings("unchecked") final Function<T,R>... getters){
		for(final var getter:getters) {
			query.sortBy(new QualifiedProperty<T, R>(this, getter));
		}
		return this;
	}
	
	public <R> Entry<T> groupBy(@SuppressWarnings("unchecked") final Function<T,R>... getters){
		for(final var getter:getters) {
			query.groupBy(new QualifiedProperty<T,R>(this,getter));
		}
		return this;
	}
	
	public <R extends Numeric> Entry<T> aggregate(final Function<T,R> getter,final Aggregator<? extends Numeric,R> aggregator){
		query.aggregate(new AggregationProperty<T,R>(this,getter,aggregator));
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
	
	public <R extends Entity> void  joinOn(final Entry<R> dest,final Property<R> property) {
		query.join(this,dest,property);
	}
	
	public void where(
			@SuppressWarnings("unchecked") final Predicate<T>...addPredicates) {
		predicates.addAll(Arrays.asList(addPredicates));
	}
			
	public boolean validate(final T entity) {
		boolean valid=true;
		for(final Predicate<T> p:predicates) {
			valid=valid && p.test(entity);
		}
		return valid;
	}
	
	String predicateClause() {
		final StringJoiner joiner=new StringJoiner(" and ");
		predicates.forEach((Predicate<? extends Entity> x)->joiner.add(x.toString()));
		return joiner.toString();
	}
	
	@Override public int hashCode() {
		return Objects.hash(entityClass.hashCode(),getAlias());
	}
	
	@SuppressWarnings("unchecked")
	@Override public boolean equals(final Object o) {
		return (o instanceof Entry entry)?
				entityClass.equals(entry.entityClass):false;
	}
	
	@Override public String toString() {
		return entityClass.getSimpleName();
	}
}
