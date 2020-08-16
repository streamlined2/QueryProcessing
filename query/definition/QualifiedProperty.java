package query.definition;

import java.util.Objects;
import java.util.function.Function;

import entity.definition.Entity;
import entity.definition.Property;

public class QualifiedProperty<T extends Entity> {
	private final Entry<T> entry;
	private final Property<T> property;
	
	public QualifiedProperty(final Entry<T> entry,final Property<T> property) {
		this.entry=entry;
		this.property=property;
	}
	
	public QualifiedProperty(final Entry<T> entry,final Function<T,?> getter){
		this(entry,new Property<T>(getter));
	}
	
	public Entry<T> getEntry() { return entry;}
	public Property<T> getProperty() { return property;}
	
	@Override public String toString() {
		return new StringBuilder().
				append(entry).append(".").
				append(property).toString();
	}
	
	@Override public int hashCode() {
		return Objects.hash(entry,property);
	}
	
	@Override public boolean equals(final Object o) {
		return (o instanceof QualifiedProperty)?
				entry.equals(((QualifiedProperty<?>)o).entry) && 
				property.equals(((QualifiedProperty<?>)o).property):
					false;
	}
}

