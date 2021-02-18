package query.definition;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import collections.FilteredIterator;
import entity.definition.Entity;
import entity.definition.Property;

/**
 * Holds query definition
 * @author Serhii Pylypenko
 *
 */

public class Query implements Iterable<Entry<? extends Entity>>{
	
	private final EntryList entries=new EntryList();
	private final PropertyList selectProperties=new PropertyList();//fetch no properties at all, if empty
	private final PropertyList sortByProperties=new PropertyList();//no ordering at all, if empty
	private final PropertyList groupByProperties=new PropertyList();//no aggregation, if empty
	private final AggregationList aggregationProperties=new AggregationList();
	private final PropertyList havingByProperies=new PropertyList();//no extra filtering after aggregation, if empty
	private final JointsMap joints=new JointsMap();//register links between data entries
	
	@SafeVarargs
	public Query(final Class<? extends Entity>...entityClasses) {
		for(int k=0;k<entityClasses.length;k++) addEntry(entityClasses[k]);
	}
	
	public Entry<? extends Entity> getEntry(final int index) {
		if(index<0 || index>=entries.size()) throw new RuntimeException(String.format("wrong entry index %d, should be within range [%d,%d]",index,0,entries.size()-1));
		return entries.get(index);
	}
	
	public <T extends Entity> Entry<T> addEntry(final Class<T> entityClass){
		return entries.add(this, entityClass);
	}
	
	public <T extends Entity,R extends Entity> void join(
			final Entry<T> entry,final Entry<R> dest,final Property<R> property) {
		joints.register(entry, dest, property);
	}
	
	@SafeVarargs
	public final void select(final QualifiedProperty<? extends Entity,?>...properties) {
		selectProperties.addProperties(properties);
	}
	
	@SafeVarargs
	public final void sortBy(final QualifiedProperty<? extends Entity,?>...properties) {
		sortByProperties.addProperties(properties);
	}
	
	@SafeVarargs
	public final void groupBy(final QualifiedProperty<? extends Entity,?>...properties) {
		groupByProperties.addProperties(properties);
	}
	
	@SafeVarargs
	public final void aggregate(final AggregationProperty<? extends Entity,?>...properties) {
		aggregationProperties.addProperties(properties);
	}
	
	@SafeVarargs
	public final void havingBy(final QualifiedProperty<? extends Entity,?>...properties) {
		havingByProperies.addProperties(properties);
	}
	
	private StringBuilder selectClause() {
		return new StringBuilder().append("SELECT ").append(selectProperties).append(" ");
	}
	
	private StringBuilder aggregationClause() {
		final StringBuilder b=new StringBuilder();
		if(!aggregationProperties.empty() && !selectProperties.empty()) {
			b.append(",");
		}
		b.append(aggregationProperties);
		if(!aggregationProperties.empty()) {
			b.append(" ");
		}
		return b;
	}
	
	private StringBuilder fromClause() {
		return new StringBuilder().append("FROM ").append(entries);//.append(entries.empty()?"":"\n");
	}
	
	private StringBuilder whereClause() {
		final StringBuilder b=new StringBuilder();
		if(countPredicates()!=0) {
			b.append("WHERE ");
			entries.forEach(x->b.append(x.predicateClause()));
			b.append("\n");
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
		if(!sortByProperties.empty()) b.append("ORDER BY ").append(sortByProperties).append(sortByProperties.empty()?"":"\n");
		return b;
	}

	private StringBuilder groupByClause() {
		final StringBuilder b=new StringBuilder();
		if(!groupByProperties.empty()) b.append("GROUP BY ").append(groupByProperties).append(groupByProperties.empty()?"":"\n");
		return b;
	}

	private StringBuilder havingByClause() {
		final StringBuilder b=new StringBuilder();
		if(!havingByProperies.empty()) b.append("HAVING BY ").append(havingByProperies).append(havingByProperies.empty()?"":"\n");
		return b;
	}
	
	public String getSQLStatement() {
		return new StringBuilder()
				.append(selectClause())
				.append(aggregationClause())
				.append(fromClause())
				.append(whereClause())
				.append(orderByClause())
				.append(groupByClause())
				.append(havingByClause())
				.toString();
	}

	@Override
	public String toString() {
		return getSQLStatement();
	}

	@Override
	public Iterator<Entry<? extends Entity>> iterator() {
		return entries.iterator();
	}
	
	public FilteredIterator<QualifiedProperty<? extends Entity,?>,Entry<? extends Entity>> selectIterator(final Entry<? extends Entity> entry){
		return selectProperties.iterator(entry);
	}
	
	public final int selectDimension() {
		return selectProperties.size();
	}
	
	public FilteredIterator<QualifiedProperty<? extends Entity,?>,Entry<? extends Entity>> sortByIterator(final Entry<? extends Entity> entry){
		return sortByProperties.iterator(entry);
	}
	
	public final int sortByDimension() {
		return sortByProperties.size();
	}
	
	public final int sortGroupByDimension() {
		return sortByProperties.merge(groupByProperties).size();
	}
	
	public FilteredIterator<QualifiedProperty<? extends Entity,?>,Entry<? extends Entity>> sortGroupByIterator(final Entry<? extends Entity> entry){
		return sortByProperties.merge(groupByProperties).iterator(entry);
	}
	
	public FilteredIterator<AggregationProperty<? extends Entity,?>,Entry<? extends Entity>> aggregationIterator(final Entry<? extends Entity> entry){
		return aggregationProperties.iterator(entry);
	}
	
	public Iterator<AggregationProperty<? extends Entity,?>> aggregationIterator() {
		return aggregationProperties.iterator();
	}
	
	public final int aggregateDimension() {
		return aggregationProperties.size();
	}
	
	public boolean sortByEmpty() {
		return sortByProperties.empty();
	}
	
	public boolean groupByEmpty() {
		return groupByProperties.empty();
	}
	
	public final Set<Entry<? extends Entity>> getEntries(){
		return entries.getEntrySet();
	}
	
	public final Optional<Link<? extends Entity,? extends Entity>> getLink(final Entry<? extends Entity> entry){
		return joints.getLink(entry); 
	}
	
	public boolean aggregatingByOrderProperties() {
		return groupByProperties.isSubList(sortByProperties);
	}
	
}
