package entity.persistence;

import entity.definition.Entity;

public interface EntityToDBMapper {

	String mapEntityToTable(final Class<? extends Entity> eClass);	
	
	String mapPropertyToAttribute(final String propertyName);

	default String mapEntityToTable(final Entity e) {
		return mapEntityToTable(e.getClass());
	}
}
