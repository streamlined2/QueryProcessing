package entity.definition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import query.definition.Entry;

/**
 * Holds data for a set of entities
 *  
 * @author Serhii Pylypenko
 *
*/
public class DataSource implements Iterable<Map.Entry<Class<? extends Entity>,EntitySource<? extends Entity>>>{
	
	private Map<Class<? extends Entity>,EntitySource<? extends Entity>> data=new HashMap<>();
	
    @SafeVarargs
	public DataSource(final EntitySource<? extends Entity>...sources) {
		for(final EntitySource<? extends Entity> eSource:sources) {
			data.put(eSource.getEntityClass(),eSource);
		}
	}
	
	public DataSource addSource(final EntitySource<? extends Entity> eSource) {
		data.put(eSource.getEntityClass(), eSource);
		return this;
	}

	@Override
	public Iterator<Map.Entry<Class<? extends Entity>,EntitySource<? extends Entity>>> iterator() {
		return data.entrySet().iterator();
	}
	
	public boolean hasDataFor(final Entry<? extends Entity> entry) {
		return hasDataFor(entry.getEntityClass());
	}
	
	public boolean hasDataFor(final Class<? extends Entity> entityClass) {
		return getDataFor(entityClass).isPresent();
	}
	
	public Optional<EntitySource<? extends Entity>> getDataFor(final Entry<? extends Entity> entry){
		return getDataFor(entry.getEntityClass());
	}
	
	public Optional<EntitySource<? extends Entity>> getDataFor(final Class<? extends Entity> entityClass){
		return Optional.ofNullable(data.get(entityClass));
	}
	
}
