package query.definition.aggregators;

import math.Numeric;

public class Total<T extends Numeric> extends Aggregator<T,T> {
	
	@Override public String getName() { 
		return "SUM";
	}
	
}
