package query.definition;

import entity.definition.Entity;

public class Link<T extends Entity,R extends Entity> {
	private final QualifiedProperty<T,R> linkProperty;
	private final Entry<R> destination;
	
	public Link(final Entry<R> destination,final QualifiedProperty<T,R> link){
		this.destination=destination;
		this.linkProperty=link;
	}
	
	public final Entry<R> getDestination(){ return destination;}
	public final QualifiedProperty<T,R> getSourceProperty(){ return linkProperty;}

	public final boolean pointsAt(final Entry<? extends Entity> target) {
		return destination.equals(target);
	}
	
}
