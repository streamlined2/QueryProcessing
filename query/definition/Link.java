package query.definition;

import java.util.Optional;

import entity.definition.Entity;
import entity.definition.Property;

public class Link<T extends Entity,R extends Entity> {
	private final QualifiedProperty<T,R> linkProperty;
	private final Entry<R> destination;
	
	public Link(final Entry<R> destination,final QualifiedProperty<T,R> link){
		this.destination=destination;
		this.linkProperty=link;
	}
	
	public Link(final Entry<T> source) {
		this.destination=null;
		this.linkProperty=new QualifiedProperty<T,R>(source,(Property<R>)null);
	}
	
	public final Entry<R> getDestination(){ return destination;}
	public final QualifiedProperty<T,R> getSourceProperty(){ return linkProperty;}
	public final Entry<T> getSource(){ return linkProperty.getEntry();}

	public Optional<R> getRelatedEntity(final Optional<? extends Entity> tupleEntity) {
		return linkProperty.getRelatedEntity(tupleEntity);
	}
	
}
