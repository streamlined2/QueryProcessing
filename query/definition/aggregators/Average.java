package query.definition.aggregators;

import math.Cardinal;
import math.Numeric;

class AverageNumericData<T extends Numeric> implements Numeric {
	
	private final T total;
	private final Cardinal count;
	
	public AverageNumericData(final T total,final Cardinal count) {
		this.total=total;
		this.count=count;
	}

	@Override public String toString() { 
		return total.divide(count).toString();
	}
	
	@Override public boolean equals(final Object o) { 
		return o instanceof AverageNumericData other? 
				total.equals(other.total) && count.equals(other.count): false;
	} 

	@Override public int compareTo(final Numeric o) {
		throw new UnsupportedOperationException("comparison operation undefined for AverageNumericData");
	}

	@Override
	public Numeric add(final Numeric add) {
		if(add instanceof AverageNumericData addData) {
			return new AverageNumericData((T)total.add(addData.total),count.add(addData.count));
		}else throw new IllegalArgumentException("parameter type for 'add' should be AverageNumericData");
	}

	@Override
	public Numeric one() {
		throw new UnsupportedOperationException("'one' undefined for type AverageNumericData");
		//return new AverageNumericData(total.one(),count.one());
	}

	@Override
	public Numeric zero() {
		throw new UnsupportedOperationException("'zero' undefined for type AverageNumericData");
		//return new AverageNumericData(total.zero(),count.zero());
	}

	@Override
	public Numeric divide(final Numeric divisor) {
		throw new UnsupportedOperationException("division operation undefined for AverageNumericData");
	}
	
}

public class Average<T extends Numeric> extends Aggregator<AverageNumericData<T>,T> {
	
	public AverageNumericData<T> map(T value) {
		return new AverageNumericData<T>(value,Cardinal.ONE);
	};
	
	protected AverageNumericData<T> accumulate(AverageNumericData<T> total,AverageNumericData<T> value) {
		return (AverageNumericData<T>)total.add(value);
	}

}
