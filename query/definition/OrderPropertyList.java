package query.definition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import collections.FilteredIterator;
import entity.definition.Entity;

public class OrderPropertyList implements Iterable<OrderProperty<? extends Entity,?>>{
	private List<OrderProperty<? extends Entity,?>> properties=new LinkedList<>();
	
	void addProperty(final OrderProperty<? extends Entity,?> property) {
		properties.add(property);
	}
	
	void addProperties(final OrderProperty<? extends Entity,?>[] properties) {
		this.properties.addAll(Arrays.asList(properties));
	}
	
	void addProperties(final List<OrderProperty<? extends Entity,?>> list) {
		this.properties.addAll(list);
	}
	
	boolean contains(final OrderProperty<? extends Entity, ?> property) {
		return properties.contains(property);
	}
	
	@Override
	public Iterator<OrderProperty<? extends Entity,?>> iterator() {
		return properties.iterator();
	}
	
	public FilteredIterator<OrderProperty<? extends Entity,?>,Entry<? extends Entity>> iterator(final Entry<? extends Entity> entry){
		return new FilteredIterator<OrderProperty<? extends Entity,?>,Entry<? extends Entity>>(
				iterator(),(p,e)->p.getEntry().equals(e),entry);
	}

	public boolean empty() {
		return properties.isEmpty();
	}
	
	public int size() {
		return properties.size();
	}

}
