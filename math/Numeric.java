package math;

import java.io.Serializable;

public interface Numeric extends Comparable<Numeric>, Serializable {
	
	Numeric add(Numeric add);
	Numeric divide(Numeric divisor);
	Numeric one();
	Numeric zero();

}
