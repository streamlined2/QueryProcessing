package entity.definition;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import query.exceptions.NoneMultipleEntitiesException;

/**
 * Entity data class holder
 * @author Serhii Pylypenko
 *
 * @param <T> entity type
 */

public class EntitySource<T extends Entity> implements Iterable<T> {
	
	private final Class<T> entityClass;
	private final SortedSet<T> data=new TreeSet<>();//HashSet //rare modifications expected, optimized for search 

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public EntitySource(final T... source) {
		entityClass=(Class<T>) source.getClass().getComponentType();
		data.addAll(Arrays.asList(source));
	}
	
	//the passed entity is suitable for this entity source
	public boolean suitable(final Optional<? extends Entity> entity) {
		assert entity.isPresent();
		return getEntityClass().isAssignableFrom(entity.get().getClass());
	}
	
	public boolean contains(final T entity) {
		return data.contains(entity);
	}
	
	public Class<T> getEntityClass(){
		return entityClass;
	}

	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}
	
	public Set<T> getData(){
		return Collections.unmodifiableSet(data);
	}
	
	private class AcceptableEntityPredicate implements Predicate<T>{
		
		final Map<Property<?>,?> attributeValues;
		
		public AcceptableEntityPredicate(final Map<Property<?>,?> attributeValues) {
			this.attributeValues=attributeValues;
		}

		@Override
		public boolean test(final T t) {
			boolean acceptable=true;
			for(final Map.Entry<Property<?>,?> propertyValue:attributeValues.entrySet()) {
				acceptable=acceptable && 
						propertyValue.getKey().getValue(Optional.of(t)).equals(propertyValue.getValue());
			}
			return acceptable;
		}
		
	}
	
	/**
	 * takes set of attribute values and returns stream of entities that have those values
	 * @param attributeValues set of attributes' values
	 * @return stream of entities that have properties set to given values
	 */
	public Stream<T> findBy(final Map<Property<?>,?> attributeValues){
		final Predicate<T> predicate=new AcceptableEntityPredicate(attributeValues); 
		return data.stream().	filter(predicate);
	}
	
	public Optional<T> first(final Map<Property<?>,?> attributeValues){
		return findBy(attributeValues).findFirst();
	}
	
	public T one(final Map<Property<?>,?> attributeValues) throws NoneMultipleEntitiesException {
		final Iterator<T> i=findBy(attributeValues).iterator();
		if(!i.hasNext()) throw new NoneMultipleEntitiesException("should be at least one entity that has properties set to given values");
		else {
			final T candidate=i.next();
			if(i.hasNext()) throw new NoneMultipleEntitiesException("should be no more than one entity that has properties set to given values");
			else return candidate; 
		}
	}
	
	public <R> T one(final Property<R> property,final R value) throws NoneMultipleEntitiesException {
		return one(Map.of(property,value));
	}
	
	public <R> T one(final Function<T,R> methodReference,final R value) throws NoneMultipleEntitiesException {
		return one(new FunctionalProperty<>(methodReference),value);
	}
	
	public <R,S> T one(
			final Property<R> property1,final R value1,
			final Property<S> property2,final S value2) throws NoneMultipleEntitiesException {
		return one(Map.of(property1,value1,property2,value2));
	}
	
	public <R,S> T one(
			final Function<T,R> methodReference1,final R value1,
			final Function<T,S> methodReference2,final S value2
			) throws NoneMultipleEntitiesException {
		return one(
				new FunctionalProperty<>(methodReference1),value1,
				new FunctionalProperty<>(methodReference2),value2);
	}
	
	
}
