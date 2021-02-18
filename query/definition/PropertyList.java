package query.definition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import collections.FilteredIterator;
import entity.definition.Entity;

public class PropertyList implements Iterable<QualifiedProperty<? extends Entity,?>>{
	private List<QualifiedProperty<? extends Entity,?>> properties=new LinkedList<>();
	
	void addProperty(final QualifiedProperty<? extends Entity,?> property) {
		properties.add(property);
	}
	
	void addProperties(final QualifiedProperty<? extends Entity,?>[] properties) {
		this.properties.addAll(Arrays.asList(properties));
	}
	
	void addProperties(final List<QualifiedProperty<? extends Entity,?>> list) {
		this.properties.addAll(list);
	}
	
	boolean contains(final QualifiedProperty<? extends Entity, ?> property) {
		return properties.contains(property);
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
	
	
	/**
	 * Checks if {@code properties} starts with same items in the same order as {@code subList}  
	 * @param subList
	 * @return true if check successful
	 */
	/*
	public boolean isSubList(final PropertyList subList) {
		if(subList.size()>size()) return false;
		else {
			boolean isSubList=true;
			var i=properties.iterator();
			for(var item:subList) {
				isSubList=isSubList && item.equals(i.next());
				if(!isSubList) return false;
			}
			return true;
		}
	}
	*/
	/**
	 * Merges two property lists into new one
	 * @param list list to merge
	 * @return resulting list that contains items from both  
	 *//*
	public PropertyList merge(final PropertyList list){
		final PropertyList result=new PropertyList();
		result.addProperties(properties);
		list.forEach(prop->{
				if(!result.contains(prop)) result.addProperty(prop);
		});
		return result;
	}*/

}
