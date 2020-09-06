package query.definition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import entity.definition.Entity;
import entity.definition.Property;

class Link<T extends Entity,R extends Entity> {
	private QualifiedProperty<T,R> link;
	private Entry<R> destination;
	
	Link(final Entry<R> destination,final QualifiedProperty<T,R> link){
		this.destination=destination;
		this.link=link;
	}
	
	Entry<R> getDestination(){ return destination;}
	QualifiedProperty<T,R> getProperty(){ return link;}
	
}

public class JointsMap implements Iterable<Link<? extends Entity,? extends Entity>>{

	private Map<Entry<? extends Entity>,Link<? extends Entity,? extends Entity>> joints=new HashMap<>();
	
	public <T extends Entity,R extends Entity> void register(
			final Entry<T> entry,final Entry<R> dest,final Property<T,R> property) {
		joints.put(entry, 
				new Link<T,R>(dest,new QualifiedProperty<T,R>(entry,property)));
	}
	
	public QualifiedProperty<? extends Entity,? extends Entity> getQualifiedProperty(final Entry<? extends Entity> entry){
		return joints.get(entry).getProperty(); 
	}
	
	public Entry<? extends Entity> getDestination(final Entry<? extends Entity> entry){
		return joints.get(entry).getDestination();
	}

	@Override
	public Iterator<Link<? extends Entity, ? extends Entity>> iterator() {
		return joints.values().iterator();
	}

}
