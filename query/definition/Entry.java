package query.definition;

import java.util.Arrays;
import java.util.Objects;
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
	
	public Iterable<Predicate<T>> predicates(){
		return predicates;
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
			@SuppressWarnings("unchecked") final String... getters){
		for(final var getter:getters) {
			query.sortBy(new OrderProperty<T, R>(new QualifiedProperty<>(this, getter),OrderProperty.OrderKind.ASCENDING));
		}
		return this;
	}
	
	public <R> Entry<T> sortBy(
			@SuppressWarnings("unchecked") final Function<T,R>... getters){
		for(final var getter:getters) {
			query.sortBy(new OrderProperty<T, R>(new QualifiedProperty<>(this, getter),OrderProperty.OrderKind.ASCENDING));
		}
		return this;
	}
	
	public <R> Entry<T> sortBy(
			@SuppressWarnings("unchecked") final QualifiedProperty<T,R>... qualifiedProperties){
		for(final var qualifiedProperty:qualifiedProperties) {
			query.sortBy(new OrderProperty<T, R>(qualifiedProperty,OrderProperty.OrderKind.ASCENDING));
		}
		return this;
	}
	
	public <R> Entry<T> sortByDesc(
			@SuppressWarnings("unchecked") final String... getters){
		for(final var getter:getters) {
			query.sortBy(new OrderProperty<T, R>(new QualifiedProperty<>(this, getter),OrderProperty.OrderKind.DESCENDING));
		}
		return this;
	}
	
	public <R> Entry<T> sortByDesc(
			@SuppressWarnings("unchecked") final Function<T,R>... getters){
		for(final var getter:getters) {
			query.sortBy(new OrderProperty<T, R>(new QualifiedProperty<>(this, getter),OrderProperty.OrderKind.DESCENDING));
		}
		return this;
	}
	
	public <R> Entry<T> sortByDesc(
			@SuppressWarnings("unchecked") final QualifiedProperty<T,R>... qualifiedProperties){
		for(final var qualifiedProperty:qualifiedProperties) {
			query.sortBy(new OrderProperty<T, R>(qualifiedProperty,OrderProperty.OrderKind.DESCENDING));
		}
		return this;
	}
	
	public <R> Entry<T> groupBy(@SuppressWarnings("unchecked") final Function<T,R>... getters){
		for(final var getter:getters) {
			query.groupBy(new QualifiedProperty<T,R>(this,getter));
		}
		return this;
	}
	
	public <R> Entry<T> groupBy(@SuppressWarnings("unchecked") final String... getters){
		for(final var getter:getters) {
			query.groupBy(new QualifiedProperty<T,R>(this,getter));
		}
		return this;
	}
	
	public <R extends Numeric> Entry<T> aggregate(final Function<T,R> getter,final Aggregator<? extends Numeric,R> aggregator){
		query.aggregate(new AggregationProperty<T,R>(this,getter,aggregator));
		return this;
	}
	
	public <R extends Numeric> Entry<T> aggregate(final String getter,final Aggregator<? extends Numeric,R> aggregator){
		query.aggregate(new AggregationProperty<T,R>(this,getter,aggregator));
		return this;
	}
	
	public <R extends Numeric> Entry<T> aggregate(final AggregationProperty<T,R> aggregationProperty){
		query.aggregate(aggregationProperty);
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
	
	@Override public int hashCode() {
		return Objects.hash(entityClass.hashCode(),getAlias());
	}
	
	@SuppressWarnings("unchecked")
	@Override public boolean equals(final Object o) {
		return (o instanceof Entry entry)?
				entityClass.equals(entry.entityClass):false;
	}
	
}
