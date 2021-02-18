package query.definition.aggregators;

import math.Numeric;

public class Count<T extends Numeric> extends Aggregator<T,T> {

	@Override
	public T map(final T value) {
		return (T)value.one();
	}

	@Override public String getName() { 
		return "COUNT";
	}
	
}
