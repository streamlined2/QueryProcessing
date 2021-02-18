package query.definition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import collections.FilteredIterator;
import entity.definition.Entity;

public class AggregationList implements Iterable<AggregationProperty<? extends Entity,?>>{
	private List<AggregationProperty<? extends Entity,?>> properties=new LinkedList<>();
	
	void addProperty(final AggregationProperty<? extends Entity,?> property) {
		properties.add(property);
	}
	
	void addProperties(final AggregationProperty<? extends Entity,?>[] properties) {
		this.properties.addAll(Arrays.asList(properties));
	}
	
	@Override
	public Iterator<AggregationProperty<? extends Entity,?>> iterator() {
		return properties.iterator();
	}
	
	public FilteredIterator<AggregationProperty<? extends Entity,?>,Entry<? extends Entity>> iterator(final Entry<? extends Entity> entry){
		return new FilteredIterator<AggregationProperty<? extends Entity,?>,Entry<? extends Entity>>(
				iterator(),(p,e)->p.getEntry().equals(e),entry);
	}

	public boolean empty() {
		return properties.isEmpty();
	}
	
	public int size() {
		return properties.size();
	}
	
}
