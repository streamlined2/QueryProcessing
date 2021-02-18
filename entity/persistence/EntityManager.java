package entity.persistence;

import java.util.Optional;
import entity.definition.Entity;
import entity.definition.Entity.PrimaryKey;
import query.exceptions.QueryException;

/**
 * Entity manager interface
 * @author Serhii Pylypenko
 *
 */
public interface EntityManager {
	
	<E extends Entity> Optional<E> find(Class<E> entityClass,PrimaryKey primaryKey) throws QueryException;
	
	void persist(Entity e) throws QueryException;
	void merge(Entity e) throws QueryException;

	default <E extends Entity> void remove(final E entity) throws QueryException {
		remove(entity.getClass(),entity.id()); 
	}
	<E extends Entity> void remove(Class<E> entityClass,PrimaryKey key) throws QueryException;
	<E extends Entity> int removeAll(Class<E> entityClass) throws QueryException;

}
