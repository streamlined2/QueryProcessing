package query.definition;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
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
	
	class PredicateList<T extends Entity> implements Iterable<Predicate<T>>{
		private final List<Predicate<T>> predicates=new LinkedList<>();
		
		void addPredicate(final Predicate<T> p) { predicates.add(p);}

		void addAll(final Collection<Predicate<T>> addList) {
			predicates.addAll(addList);//addList.forEach(x->predicates.add(x));
		}

		@Override
		public Iterator<Predicate<T>> iterator() {
			return predicates.iterator();
		}
		
		int size() {
			return predicates.size();
		}
		
		@Override
		public String toString() {
			StringJoiner joiner=new StringJoiner(" and ");
			predicates.forEach(x->joiner.add(x.toString()));
			return joiner.toString();
		}
	}
	
	public class Entry<T extends Entity> {
		private final Source<T> source;
		private final int index;
		private final PredicateList<T> predicates;
		
		Entry(final Source<T> source,final int index){
			this.source=source;
			this.index=index;
			predicates=new PredicateList<T>();
		}
		
		void join(final Entry<? extends Entity> dest) {
			joints.put(this, dest);
		}
		
		@SafeVarargs
		final void where(final Predicate<T>...addPredicates) {
			predicates.addAll(Arrays.asList(addPredicates));
		}
				
		boolean validate(final T entity) {
			boolean valid=true;
			for(final Predicate<T> p:predicates) {
				valid=valid && p.test(entity);
			}
			return valid;
		}
		
		StringBuilder predicateClause() {
			final StringBuilder b=new StringBuilder();
			predicates.forEach(x->b.append(x));
			return b;
		}
		
		@Override public int hashCode() {
			return source.hashCode();
		}
		
		@SuppressWarnings("unchecked")
		@Override public boolean equals(final Object o) {
			return (o instanceof Entry)?
					source.equals(((Entry<T>)o).source):false;
		}
		
		Character getAlias() {
			return Character.valueOf((char)('A'+index));
		}
		
		@Override public String toString() {
			return 
					new StringBuilder().
					append(getAlias()).append('.').
					append(source.getEntityClass().getSimpleName()).toString();
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
		
		void addProperty(final QualifiedProperty<? extends Entity> property) {
			properties.add(property);
		}
		
		void addProperties(final QualifiedProperty<? extends Entity>[] properties) {
			this.properties.addAll(Arrays.asList(properties));
		}
		
		@Override
		public String toString() {
			final StringBuilder b=new StringBuilder();
			properties.forEach(x->b.append(x));
			return b.toString();
		}

		@Override
		public Iterator<QualifiedProperty<? extends Entity>> iterator() {
			return properties.iterator();
		}
		
		public Iterator<QualifiedProperty<? extends Entity>> iterator(final Entry<? extends Entity> entry){
			return new Iterator<QualifiedProperty<? extends Entity>>() {
				
				private Iterator<QualifiedProperty<? extends Entity>> iterator=iterator();
				private QualifiedProperty<? extends Entity> stash=null;
				
				private QualifiedProperty<? extends Entity> findNext() {
					while(iterator.hasNext()) {
						final QualifiedProperty<? extends Entity> nextOne=iterator.next();
						if(nextOne.getEntry().equals(entry)) return nextOne;
					}
					return null;
				}
				
				@Override
				public boolean hasNext() {
					return (stash=findNext())!=null;
				}

				@Override
				public QualifiedProperty<? extends Entity> next() {
					return stash;
				}
				
			};
		}

		public boolean empty() {
			return properties.isEmpty();
		}		
	}
	
	private class EntryList implements Iterable<Entry<? extends Entity>> {
		private final List<Entry<? extends Entity>> entries=new LinkedList<>();
		
		boolean empty() {
			return entries.isEmpty();
		}
		
		void add(final Entry<? extends Entity> entry) {
			entries.add(entry);
		}
		
		Entry<? extends Entity> get(final int index){
			return entries.get(index);
		}
		
		int size() {
			return entries.size();
		}
		
		@Override
		public String toString() {
			StringJoiner joiner=new StringJoiner(",");
			entries.forEach(x->joiner.add(x.toString()));
			return joiner.toString();
		}

		@Override
		public Iterator<Entry<? extends Entity>> iterator() {
			return entries.iterator();
		}
	}
	
	private final EntryList entries=new EntryList();
	private PropertyList selectProperties=new PropertyList();//fetch no properties at all, if empty
	private PropertyList sortByProperties=new PropertyList();//no ordering at all, if empty
	private PropertyList groupByProperties=new PropertyList();//no aggregation, if empty
	private PropertyList havingByProperies=new PropertyList();//no extra filtering after aggregation, if empty
	private Map<Entry<? extends Entity>,Entry<? extends Entity>> joints=new HashMap<>();//to register links between data entries
	
	@SafeVarargs
	public Query(final Source<? extends Entity>...sources) {
		for(int k=0;k<sources.length;k++) addEntry(sources[k]);
	}
	
	public Entry<? extends Entity> getEntry(final int index) {
		if(index<0 || index>=entries.size()) throw new RuntimeException(String.format("incorrect entry index %d, it should be within range [%d,%d]",index,0,entries.size()-1));
		return entries.get(index);
	}
	
	public <T extends Entity> Entry<T> addEntry(final Source<T> source) {
		final Entry<T> entry=new Entry<>(source,entries.size());
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
	
	@SafeVarargs
	public final void havingBy(final QualifiedProperty<? extends Entity>...properties) {
		havingByProperies.addProperties(properties);
	}
	
	private StringBuilder selectClause() {
		return new StringBuilder().append("SELECT ").append(selectProperties);
	}
	
	private StringBuilder fromClause() {
		return new StringBuilder().append("FROM ").append(entries);
	}
	
	private String whereClause() {
		if(countPredicates()==0) {
			return "";
		}else {
			final StringBuilder b=new StringBuilder().append("WHERE ");
			entries.forEach(x->b.append(x.predicateClause()));
			return b.toString();			
		}
	}
	
	private int countPredicates() {
		int count=0;
		for(final Entry<? extends Entity> e:entries) count+=e.predicates.size();
		return count;
	}
	
	private String orderByClause() {
		return sortByProperties.empty()?"":new StringBuilder().append("ORDER BY ").append(sortByProperties).toString();
	}

	private Object groupByClause() {
		return groupByProperties.empty()?"":new StringBuilder().append("GROUP BY ").append(groupByProperties).toString();
	}

	private Object havingByClause() {
		return havingByProperies.empty()?"":new StringBuilder().append("HAVING BY ").append(havingByProperies).toString();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(selectClause()).append('\n')
				.append(fromClause()).append('\n')
				.append(whereClause()).append('\n')
				.append(orderByClause()).append('\n')
				.append(groupByClause()).append('\n')
				.append(havingByClause()).append('\n')
				.toString();
	}

}
