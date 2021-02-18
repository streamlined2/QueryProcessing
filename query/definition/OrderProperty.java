package query.definition;

import java.util.Objects;
import entity.definition.Entity;
import entity.definition.Property;

public class OrderProperty<T extends Entity,R> {
	
	public enum OrderKind { ASCENDING, DESCENDING};
	
	private final QualifiedProperty<T, R> qualifiedProperty;
	private final OrderKind orderKind;
	
	public OrderProperty(final QualifiedProperty<T, R> qualifiedProperty,final OrderKind orderKind) {
		this.qualifiedProperty=qualifiedProperty;
		this.orderKind=orderKind;
	}
	
	public OrderKind getOrderKind() { return orderKind;}
	public QualifiedProperty<T,R> getQualifiedProperty() { return qualifiedProperty;}
	
	public Entry<T> getEntry() { return qualifiedProperty.getEntry();}
	public Property<R> getProperty() { return qualifiedProperty.getProperty();}
	
	@Override public int hashCode() {
		return Objects.hash(qualifiedProperty,orderKind);
	}
	
	@Override public boolean equals(final Object o) {
		return (o instanceof OrderProperty prop)?
				qualifiedProperty.equals(prop.qualifiedProperty) && orderKind.equals(prop.orderKind):
					false;
	}

}
