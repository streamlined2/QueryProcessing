package query.definition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import entity.definition.Entity;
import utils.FilteredIterator;

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
	
	public FilteredIterator<QualifiedProperty<? extends Entity,?>,Entry<? extends Entity>> iterator(final Entry<? extends Entity> entry){
		return new FilteredIterator<QualifiedProperty<? extends Entity,?>,Entry<? extends Entity>>(
				iterator(),(p,e)->p.getEntry().equals(e),entry);
	}

	public boolean empty() {
		return properties.isEmpty();
	}
	
	public int size() {
		return properties.size();
	}
	
}
