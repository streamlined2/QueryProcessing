package query.definition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import entity.definition.Entity;
import entity.definition.Property;

public class JointsMap implements Iterable<Link<? extends Entity,? extends Entity>>{

	private Map<Entry<? extends Entity>,Link<? extends Entity,? extends Entity>> joints=new HashMap<>();
	
	public <T extends Entity,R extends Entity> void register(
			final Entry<T> entry,final Entry<R> dest,final Property<T,R> property) {
		joints.put(entry, 
				new Link<T,R>(dest,new QualifiedProperty<T,R>(entry,property)));
	}
	
	public Optional<Link<? extends Entity,? extends Entity>> getLink(final Entry<? extends Entity> entry){
		return Optional.ofNullable(joints.get(entry)); 
	}
	
	@Override
	public Iterator<Link<? extends Entity, ? extends Entity>> iterator() {
		return joints.values().iterator();
	}

}
