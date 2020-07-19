package query.definition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import entity.definition.Property;
import entity.definition.Entity;
import entity.definition.Source;

/**
 * Holds query definition
 * @author Serhii Pylypenko
 *
 */

public class Query {
	
	public class Entry<T extends Entity> {
		private final Source<T> source;
		private final List<Predicate<T>> predicates;
		
		Entry(final Source<T> source){
			this.source=source;
			predicates=new LinkedList<Predicate<T>>();
		}
		
		@SafeVarargs
		public final void where(final Predicate<T>...predicates) {
			this.predicates.addAll(Arrays.asList(predicates));
		}
				
		public boolean validate(final T entity) {
			boolean valid=true;
			for(final Predicate<T> p:predicates) {
				valid=valid && p.test(entity);
			}
			return valid;
		}
		
		@Override public int hashCode() {
			return source.hashCode();
		}
		
		@SuppressWarnings("unchecked")
		@Override public boolean equals(final Object o) {
			return (o instanceof Entry)?source.equals(((Entry<T>)o).source):false;
		}
		
	}
	
	public class QualifiedProperty<T extends Entity> {
		private final Entry<T> entry;
		private final Property<T> property;
		
		QualifiedProperty(final Entry<T> entry,final Property<T> property) {
			this.entry=entry;
			this.property=property;
		}
		
		QualifiedProperty(final Entry<T> entry,final Function<T,?> getter){
			this(entry,new Property<T>(getter));
		}
		
		Entry<T> getEntry() { return entry;}
		Property<T> getProperty() { return property;}
		
		@Override public int hashCode() {
			return Objects.hash(entry,property);
		}
		
		@Override public boolean equals(final Object o) {
			return (o instanceof QualifiedProperty)?
					entry.equals(((QualifiedProperty<?>)o).entry) && 
					property.equals(((QualifiedProperty<?>)o).property):
						false;
		}
	}
	
	private class PropertyList implements Iterable <QualifiedProperty<? extends Entity>>{
		private List<QualifiedProperty<? extends Entity>> properties=new LinkedList<>();
		
		//PropertyList() {}//default constructor
		void addProperty(final QualifiedProperty<? extends Entity> property) {
			properties.add(property);
		}
		
		void addProperties(final QualifiedProperty<? extends Entity>[] properties) {
			this.properties.addAll(Arrays.asList(properties));
		}

		@Override
		public Iterator<QualifiedProperty<? extends Entity>> iterator() {
			return properties.iterator();
		}
		
	}
	
	private final List<Entry<? extends Entity>> entries=new LinkedList<>();
	private PropertyList selectProperties=new PropertyList();//fetch all properties of all entities, if empty
	private PropertyList sortByProperties=new PropertyList();//no ordering at all, if empty
	private PropertyList groupByProperties=new PropertyList();//no aggregation, if empty
	
	@SafeVarargs
	public Query(final Source<? extends Entity>...sources) {
		for(int k=0;k<sources.length;k++) addEntry(sources[k]);
	}
	
	public Entry<? extends Entity> getEntry(final int index) {
		if(index<0 || index>=entries.size()) throw new RuntimeException(String.format("incorrect entry index %d, it should be within range [%d,%d]",index,0,entries.size()-1));
		return entries.get(index);
	}
	
	public <T extends Entity> Entry<T> addEntry(final Source<T> source) {
		final Entry<T> entry=new Entry<>(source);
		entries.add(entry);
		return entry;
	}
	
	@SafeVarargs
	public final void select(final QualifiedProperty<? extends Entity>...properties) {
		selectProperties.addProperties(properties);
	}
	
	@SafeVarargs
	public final void sortBy(final QualifiedProperty<? extends Entity>...properties) {
		sortByProperties.addProperties(properties);
	}
	
	@SafeVarargs
	public final void groupBy(final QualifiedProperty<? extends Entity>...properties) {
		groupByProperties.addProperties(properties);
	}

}
