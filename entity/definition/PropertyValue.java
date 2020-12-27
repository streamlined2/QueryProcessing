package entity.definition;

public class PropertyValue<R> {
	
	private final Property<R> property;
	private final R value;
	
	public PropertyValue(final Property<R> property,final R value) {
		this.property=property;
		this.value=value;
	}
	
	public R getValue() {
		return value;
	}
	
	public Property<R> getProperty(){
		return property;
	}
	
	@Override public int hashCode() {
		return property.hashCode();
	}
	
	@Override public boolean equals(final Object o) {
		return o instanceof PropertyValue pValue?
				getProperty().equals(pValue.getProperty()):
					false;
	}

}
