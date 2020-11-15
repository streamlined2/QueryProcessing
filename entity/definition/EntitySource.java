package entity.definition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
		/*
		 * for(final T src:source) { data.add(src); }
		 */
	}
	
	//the passed entity is suitable for this entity source
	public boolean suitable(final Entity entity) {
		return getEntityClass().isAssignableFrom(entity.getClass());
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
	
}
