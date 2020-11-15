package query.definition;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import entity.definition.Entity;
import entity.definition.FunctionalProperty;
import entity.definition.GetterProperty;
import entity.definition.Property;

public class QualifiedProperty<T extends Entity,R> {
	private final Entry<T> entry;
	private final Property<R> property;
	
	public QualifiedProperty(final Entry<T> entry,final Property<R> property) {
		this.entry=entry;
		this.property=property;
	}
	
	public QualifiedProperty(final Entry<T> entry,final Function<T,R> getter){
		this(entry,new FunctionalProperty<T,R>(getter));
	}
	
	public QualifiedProperty(final Entry<T> entry,final String methodName){
		this(entry,new GetterProperty<T,R>(entry.getEntityClass(),methodName));
	}
	
	public Entry<T> getEntry() { return entry;}
	public Property<R> getProperty() { return property;}
	
	public Optional<R> getRelatedEntity(final Optional<? extends Entity> source) {
		return Optional.ofNullable(property.getValue(source));
	}
	
	@Override public String toString() {
		return new StringBuilder().
				append(entry.getAlias()).append(".").
				append(property).toString();
	}
	
	@Override public int hashCode() {
		return Objects.hash(entry,property);
	}
	
	@SuppressWarnings("unchecked")
	@Override public boolean equals(final Object o) {
		return (o instanceof QualifiedProperty)?
				entry.equals(((QualifiedProperty<T,R>)o).entry) && 
				property.equals(((QualifiedProperty<T,R>)o).property):
					false;
	}
}

