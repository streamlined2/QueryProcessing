package query.definition.aggregators;

import math.Numeric;

public abstract class Aggregator<R extends Numeric,T extends Numeric> {
	
	public R map(T value) {
		return (R)value;
	};
	
	protected R accumulate(R total,R value) {
		return (R)total.add(value);
	}

	public final R apply(Numeric accumulated,Numeric value) {
		return accumulate((R)accumulated,map((T)value));
	}

}
