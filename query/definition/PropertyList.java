package query.definition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import entity.definition.Entity;

class PropertyList implements Iterable<QualifiedProperty<? extends Entity,?>>{
	private List<QualifiedProperty<? extends Entity,?>> properties=new LinkedList<>();
	
	void addProperty(final QualifiedProperty<? extends Entity,?> property) {
		properties.add(property);
	}
	
	void addProperties(final QualifiedProperty<? extends Entity,?>[] properties) {
		this.properties.addAll(Arrays.asList(properties));
	}
	
	@Override
	public String toString() {
		final StringJoiner joiner=new StringJoiner(",");
		properties.forEach(x->joiner.add(x.toString()));
		return joiner.toString();
	}

	@Override
	public Iterator<QualifiedProperty<? extends Entity,?>> iterator() {
		return properties.iterator();
	}
	
	public Iterator<QualifiedProperty<? extends Entity,?>> iterator(final Entry<? extends Entity> entry){
		return new Iterator<QualifiedProperty<? extends Entity,?>>() {
			
			private Iterator<QualifiedProperty<? extends Entity,?>> iterator=iterator();
			private QualifiedProperty<? extends Entity,?> stash=null;
			
			private QualifiedProperty<? extends Entity,?> findNext() {
				while(iterator.hasNext()) {
					final QualifiedProperty<? extends Entity,?> nextOne=iterator.next();
					if(nextOne.getEntry().equals(entry)) return nextOne;
				}
				return null;
			}
			
			@Override
			public boolean hasNext() {
				return (stash=findNext())!=null;
			}

			@Override
			public QualifiedProperty<? extends Entity,?> next() {
				return stash;
			}
			
		};
	}

	public boolean empty() {
		return properties.isEmpty();
	}		
}
