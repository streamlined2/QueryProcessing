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
	private final OrderPropertyList sortByProperties=new OrderPropertyList();//no ordering at all, if empty
	private final PropertyList groupByProperties=new PropertyList();//no aggregation, if empty
	private final AggregationList aggregationProperties=new AggregationList();
	private final PropertyList havingByProperties=new PropertyList();//no extra filtering after aggregation, if empty
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
	public final void sortBy(final OrderProperty<? extends Entity,?>...properties) {
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
		havingByProperties.addProperties(properties);
	}
	
	public int countPredicates() {
		int count=0;
		for(final Entry<? extends Entity> e:entries) count+=e.getPredicateCount();
		return count;
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
	
	public FilteredIterator<OrderProperty<? extends Entity,?>,Entry<? extends Entity>> sortByIterator(final Entry<? extends Entity> entry){
		return sortByProperties.iterator(entry);
	}
	
	public final int sortByDimension() {
		return sortByProperties.size();
	}
	
	/*
	public final int sortGroupByDimension() {
		return sortByProperties.merge(groupByProperties).size();
	}
	
	public FilteredIterator<QualifiedProperty<? extends Entity,?>,Entry<? extends Entity>> sortGroupByIterator(final Entry<? extends Entity> entry){
		return sortByProperties.merge(groupByProperties).iterator(entry);
	}
	*/
	
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
	
	public boolean containsEntry(final Entry<? extends Entity> entry) {
		return entries.contains(entry);
	}
	
	public final Optional<Link<? extends Entity,? extends Entity>> getLink(final Entry<? extends Entity> entry){
		return joints.getLink(entry); 
	}
	/*
	public boolean aggregatingByOrderProperties() {
		return groupByProperties.isSubList(sortByProperties);
	}*/

	public Iterable<Entry<? extends Entity>> entries() {
		return entries;
	}

	public Iterable<QualifiedProperty<? extends Entity,?>> selectProperties() {
		return selectProperties;
	}
	
	public Iterable<OrderProperty<? extends Entity,?>> sortByProperties() {
		return sortByProperties;
	}
	
	public Iterable<QualifiedProperty<? extends Entity,?>> groupByProperties() {
		return groupByProperties;
	}
	
	public Iterable<AggregationProperty<? extends Entity,?>> aggregationProperties() {
		return aggregationProperties;
	}
	
	public Iterable<QualifiedProperty<? extends Entity,?>> havingByProperties() {
		return havingByProperties;
	}
	
}
