package query.definition;

import java.util.HashMap;
import java.util.Map;
import entity.definition.Entity;
import entity.definition.Source;

/**
 * Holds query definition
 * @author Serhii Pylypenko
 *
 */

public class Query {
	
	private final EntryList entries=new EntryList();
	private final PropertyList selectProperties=new PropertyList();//fetch no properties at all, if empty
	private final PropertyList sortByProperties=new PropertyList();//no ordering at all, if empty
	private final PropertyList groupByProperties=new PropertyList();//no aggregation, if empty
	private final PropertyList havingByProperies=new PropertyList();//no extra filtering after aggregation, if empty
	private final Map<Entry<? extends Entity>,Entry<? extends Entity>> joints=new HashMap<>();//to register links between data entries
	
	@SafeVarargs
	public Query(final Source<? extends Entity>...sources) {
		for(int k=0;k<sources.length;k++) addEntry(sources[k]);
	}
	
	public Entry<? extends Entity> getEntry(final int index) {
		if(index<0 || index>=entries.size()) throw new RuntimeException(String.format("wrong entry index %d, should be within range [%d,%d]",index,0,entries.size()-1));
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
	
	private StringBuilder whereClause() {
		final StringBuilder b=new StringBuilder();
		if(countPredicates()!=0) {
			b.append("WHERE ");
			entries.forEach(x->b.append(x.predicateClause()));
		}
		return b;			
	}
	
	private int countPredicates() {
		int count=0;
		for(final Entry<? extends Entity> e:entries) count+=e.getPredicateCount();
		return count;
	}
	
	private StringBuilder orderByClause() {
		final StringBuilder b=new StringBuilder();
		if(!sortByProperties.empty()) b.append("ORDER BY ").append(sortByProperties);
		return b;
	}

	private StringBuilder groupByClause() {
		final StringBuilder b=new StringBuilder();
		if(!groupByProperties.empty()) b.append("GROUP BY ").append(groupByProperties);
		return b;
	}

	private StringBuilder havingByClause() {
		final StringBuilder b=new StringBuilder();
		if(!havingByProperies.empty()) b.append("HAVING BY ").append(havingByProperies);
		return b;
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
