package query.definition;

import java.util.StringJoiner;
import java.util.function.Function;

import entity.definition.Entity;
import entity.definition.FunctionalProperty;
import entity.definition.GetterProperty;
import entity.definition.Property;
import math.Cardinal;
import math.Numeric;
import math.Real;
import query.definition.aggregators.Aggregator;
import query.definition.aggregators.Average;
import query.definition.aggregators.Count;
import query.definition.aggregators.Total;

public class AggregationProperty<T extends Entity,R extends Numeric> extends QualifiedProperty<T, R> {
	
	private final Aggregator<? extends Numeric,R> aggregator;
	
	public final static Total<Cardinal> INTEGER_TOTAL=new Total<>();
	public final static Total<Real> DOUBLE_TOTAL=new Total<>();
	public final static Count<Cardinal> INTEGER_COUNT=new Count<>();
	public final static Average<Real> DOUBLE_AVERAGE=new Average<>();
	
	public AggregationProperty(final Entry<T> entry,final Property<R> property,final Aggregator<? extends Numeric,R> aggregator) {
		super(entry,property);
		this.aggregator=aggregator;
	}
	
	public AggregationProperty(final Entry<T> entry,final Function<T,R> getter,final Aggregator<? extends Numeric,R> accumulator){
		this(entry,new FunctionalProperty<T,R>(getter),accumulator);
	}
	
	public AggregationProperty(final Entry<T> entry,final String methodName,final Aggregator<? extends Numeric,R> accumulator){
		this(entry,new GetterProperty<T,R>(entry.getEntityClass(),methodName),accumulator);
	}
	
	public Numeric aggregate(final Numeric accumulated,final Numeric supplied) {
		return aggregator.apply(accumulated,supplied);
	}
	
	public Numeric map(final Numeric data) {
		return aggregator.map((R)data);
	}
	
	@Override public String toString() {
		return new StringJoiner(":", "[", "]").add(super.toString()).add(aggregator.toString()).toString();
	}
	
	@Override public int hashCode() {
		return super.hashCode()*31+aggregator.hashCode();
	}
	
	@Override public boolean equals(final Object o) {
		return (o instanceof AggregationProperty prop)?
				super.equals(prop) && aggregator.equals(prop.aggregator):
					false;
	}

}
