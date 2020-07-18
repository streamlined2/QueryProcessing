package query.definition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import entity.definition.Entity;

/**
 * Holds list of entities that query operates on
 * @author Serhii Pylypenko
 *
 */

class AliasedEntity {
	private final String alias;
	private final Class<? extends Entity> entityType;
	
	AliasedEntity(final String alias,final Class<? extends Entity> entityType) {
		this.alias=alias;
		this.entityType=entityType;
	}
	
	String getAlias() { return alias;}
	Class<? extends Entity> getEntityType() { return entityType;}

	@Override public int hashCode() {
		return Objects.hash(alias,entityType);
	}
	@Override public boolean equals(final Object obj) {
		if(obj instanceof AliasedEntity) {
			AliasedEntity e=(AliasedEntity)obj;
			return alias.equals(e.alias) && entityType.equals(e.entityType);
		}else {
			return false;
		}
	}
}

class EntityList implements Iterable<AliasedEntity> {
	
	private final Map<String,Class<? extends Entity>> entities=new HashMap<>();
	
	EntityList(final AliasedEntity[] entries){
		for(AliasedEntity entry:entries) {
			entities.put(entry.getAlias(), entry.getEntityType());
		}
	}

	@Override
	public Iterator<AliasedEntity> iterator() {
		return new Iterator<AliasedEntity>() {
			final Iterator<Map.Entry<String, Class<? extends Entity>>> iterator=entities.entrySet().iterator();

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public AliasedEntity next() {
				final Map.Entry<String, Class<? extends Entity>> entry=iterator.next();
				return new AliasedEntity(entry.getKey(),entry.getValue());
			}
			
		};
	}

}
