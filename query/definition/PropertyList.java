package query.definition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import entity.definition.Entity;
import entity.definition.Property;

/**
 * Holds list of properties that query fetches and sorts by
 * @author Serhii Pylypenko
 *
 */
class AliasedProperty<T extends Entity> {
	private final AliasedEntity entity;
	private Property<T> property;
	
	AliasedProperty(final AliasedEntity entity,final Property<T> property){
		this.entity=entity;
		this.property=property;
	}
	
	AliasedEntity getEntity() {
		return entity;
	}
	Property<T> getProperty(){
		return property;
	}
	
	@Override public boolean equals(final Object obj) {
		if(obj instanceof AliasedProperty) {
			AliasedProperty<T> prop=(AliasedProperty<T>)obj;
			return entity.equals(prop.entity) && property.equals(prop.property);
		}else return false;
	}
	
	@Override public int hashCode() {
		return Objects.hash(entity,property);
	}
	
}

class PropertyList implements Iterable<AliasedProperty<? extends Entity>> {
	
	private Map<AliasedEntity,Property> properties=new HashMap<>();
	
	public PropertyList(final AliasedProperty[] entries) {
		for(AliasedProperty entry:entries) {
			properties.put(entry.getEntity(), entry.getProperty());
		}
	}

	@Override
	public Iterator<AliasedProperty> iterator() {
		return new Iterator<AliasedProperty>() {
			final Iterator<Map.Entry<AliasedEntity, Property>> iterator=properties.entrySet().iterator();

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public AliasedProperty next() {
				Map.Entry<AliasedEntity, Property> entry=iterator.next();
				return new AliasedProperty(entry.getKey(),entry.getValue());
			}
			
		};
	}
	
	

}
